package com.leteatgo.global.batch.meeting;

import static com.leteatgo.global.constants.BeanPrefix.REMIND_MEETING_COMING_SOON;
import static com.leteatgo.global.constants.Notification.MEETING_REMIND_SOON;

import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.entity.MeetingParticipant;
import com.leteatgo.domain.meeting.repository.MeetingRepository;
import com.leteatgo.domain.meeting.type.MeetingStatus;
import com.leteatgo.domain.notification.event.NotificationEvent;
import com.leteatgo.domain.notification.event.NotificationEventPublisher;
import com.leteatgo.domain.notification.type.NotificationType;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class RemindUpcomingMeetingsConfig extends DefaultBatchConfiguration {

    private final static Integer CHUNK_SIZE = 100;

    private final MeetingRepository meetingRepository;
    private final NotificationEventPublisher notificationEventPublisher;

    @Bean(REMIND_MEETING_COMING_SOON + "Job")
    public Job job() {
        return new JobBuilder(REMIND_MEETING_COMING_SOON + "Job", jobRepository())
                .incrementer(new RunIdIncrementer())
                .start(remindUpcomingMeetingsStep())
                .build();
    }

    @Bean(REMIND_MEETING_COMING_SOON + "Step")
    public Step remindUpcomingMeetingsStep() {
        return new StepBuilder(REMIND_MEETING_COMING_SOON + "Step", jobRepository())
                .<Meeting, Meeting>chunk(CHUNK_SIZE, getTransactionManager())
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean(REMIND_MEETING_COMING_SOON + "Reader")
    public ItemReader<Meeting> reader() {
        return new ItemReader<>() {
            private Iterator<Meeting> meetingIterator;

            @Override
            public Meeting read() {
                if (meetingIterator == null) {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime oneHourLater = now.plusHours(1);
                    meetingIterator = meetingRepository.findMeetingsForRemind(now, oneHourLater,
                            MeetingStatus.IN_PROGRESS).iterator();
                }

                if (meetingIterator.hasNext()) {
                    return meetingIterator.next();
                } else {
                    return null;
                }
            }
        };
    }

    @Bean(REMIND_MEETING_COMING_SOON + "Processor")
    public ItemProcessor<Meeting, Meeting> processor() {
        return meeting -> meeting;
    }


    @Bean(REMIND_MEETING_COMING_SOON + "Writer")
    public ItemWriter<Meeting> writer() {
        return meetings -> {
            for (Meeting meeting : meetings) {
                publishNotificationForRemindMeeting(meeting);
            }
        };
    }

    private void publishNotificationForRemindMeeting(Meeting meeting) {
        List<MeetingParticipant> participants = meeting.getMeetingParticipants();

        for (MeetingParticipant participant : participants) {
            long minutesUntilMeeting = ChronoUnit.MINUTES.between(LocalDateTime.now(),
                    meeting.getStartDateTime());
            String message = String.format(MEETING_REMIND_SOON, meeting.getName(),
                    minutesUntilMeeting);
            NotificationEvent event = NotificationEvent.builder()
                    .userId(participant.getMember().getId().toString())
                    .message(message)
                    .type(NotificationType.REMIND)
                    .relatedUrl("/" + meeting.getId())
                    .build();
            notificationEventPublisher.publishEvent(event);
            log.info("NotificationEvent published: {}", event);
        }
    }
}
