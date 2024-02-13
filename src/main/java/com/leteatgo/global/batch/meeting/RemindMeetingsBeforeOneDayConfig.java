package com.leteatgo.global.batch.meeting;

import static com.leteatgo.global.constants.BeanPrefix.REMIND_MEETING_BEFORE_ONE_DAY;
import static com.leteatgo.global.constants.Notification.MEETING_REMIND_ONE_DAY_BEFORE;

import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.entity.MeetingParticipant;
import com.leteatgo.domain.meeting.repository.MeetingRepository;
import com.leteatgo.domain.meeting.type.MeetingStatus;
import com.leteatgo.domain.notification.event.NotificationEvent;
import com.leteatgo.domain.notification.event.NotificationEventPublisher;
import com.leteatgo.domain.notification.type.NotificationType;
import java.time.LocalDateTime;
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
public class RemindMeetingsBeforeOneDayConfig extends DefaultBatchConfiguration {

    private final static Integer CHUNK_SIZE = 100;

    private final MeetingRepository meetingRepository;
    private final NotificationEventPublisher notificationEventPublisher;

    @Bean(REMIND_MEETING_BEFORE_ONE_DAY + "Job")
    public Job job() {
        return new JobBuilder(REMIND_MEETING_BEFORE_ONE_DAY + "Job", jobRepository())
                .incrementer(new RunIdIncrementer())
                .start(remindMeetingBeforeOneDayStep())
                .build();
    }

    @Bean(REMIND_MEETING_BEFORE_ONE_DAY + "Step")
    public Step remindMeetingBeforeOneDayStep() {
        return new StepBuilder(REMIND_MEETING_BEFORE_ONE_DAY + "Step", jobRepository())
                .<Meeting, Meeting>chunk(CHUNK_SIZE, getTransactionManager())
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean(REMIND_MEETING_BEFORE_ONE_DAY + "Reader")
    public ItemReader<Meeting> reader() {
        return new ItemReader<>() {
            private Iterator<Meeting> meetingIterator;

            @Override
            public Meeting read() {
                if (meetingIterator == null) {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime oneDayLater = now.plusDays(1)
                            .plusHours(4); // 매일 저녁 8시에 배치가 수행되기 때문에 4시간을 더함
                    meetingIterator = meetingRepository.findMeetingsForRemind(
                            now, oneDayLater, MeetingStatus.IN_PROGRESS).iterator();
                }

                if (meetingIterator.hasNext()) {
                    return meetingIterator.next();
                } else {
                    return null;
                }
            }
        };
    }

    @Bean(REMIND_MEETING_BEFORE_ONE_DAY + "Processor")
    public ItemProcessor<Meeting, Meeting> processor() {
        return meeting -> meeting;
    }


    @Bean(REMIND_MEETING_BEFORE_ONE_DAY + "Writer")
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
            String message = String.format(MEETING_REMIND_ONE_DAY_BEFORE, meeting.getName());
            NotificationEvent event = NotificationEvent.builder()
                    .userId(participant.getMember().getId().toString())
                    .message(message)
                    .type(NotificationType.REMIND)
                    .relatedUrl("/api/meetings/detail/" + meeting.getId()) // TODO: 프론트 URL로 변경
                    .build();
            notificationEventPublisher.publishEvent(event);
            log.info("NotificationEvent published: {}", event);
        }
    }
}
