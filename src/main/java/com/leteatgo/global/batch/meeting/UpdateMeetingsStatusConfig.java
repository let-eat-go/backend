package com.leteatgo.global.batch.meeting;

import static com.leteatgo.global.constants.BeanPrefix.UPDATE_MEETING_STATUS;
import static com.leteatgo.global.constants.Notification.MEETING_CANCEL;

import com.leteatgo.domain.chat.event.ChatRoomEventPublisher;
import com.leteatgo.domain.chat.event.dto.CloseChatRoomEvent;
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
import org.springframework.batch.core.configuration.annotation.JobScope;
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
public class UpdateMeetingsStatusConfig extends DefaultBatchConfiguration {

    private final static Integer CHUNK_SIZE = 100;

    private final MeetingRepository meetingRepository;
    private final ChatRoomEventPublisher chatRoomEventPublisher;
    private final NotificationEventPublisher notificationEventPublisher;

    @Bean(UPDATE_MEETING_STATUS + "Job")
    public Job job() {
        return new JobBuilder(UPDATE_MEETING_STATUS + "Job", jobRepository())
                .incrementer(new RunIdIncrementer())
                .start(updateMeetingStatusStep())
                .build();
    }

    @Bean(UPDATE_MEETING_STATUS + "Step")
    @JobScope
    public Step updateMeetingStatusStep() {
        return new StepBuilder(UPDATE_MEETING_STATUS + "Step", jobRepository())
                .<Meeting, Meeting>chunk(CHUNK_SIZE, getTransactionManager())
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean(UPDATE_MEETING_STATUS + "Reader")
    public ItemReader<Meeting> reader() {
        return new ItemReader<>() {
            private Iterator<Meeting> meetingIterator;

            @Override
            public Meeting read() {
                if (meetingIterator == null) {
                    LocalDateTime now = LocalDateTime.now();
                    meetingIterator = meetingRepository.findMeetingsForUpdateStatus(
                            now, MeetingStatus.BEFORE).iterator();
                }

                if (meetingIterator.hasNext()) {
                    return meetingIterator.next();
                } else {
                    return null;
                }
            }
        };
    }

    @Bean(UPDATE_MEETING_STATUS + "Processor")
    public ItemProcessor<Meeting, Meeting> processor() {
        return meeting -> {
            if (meeting.getCurrentParticipants() < meeting.getMinParticipants()) {
                meeting.cancel();
                chatRoomEventPublisher.publishCloseChatRoom(
                        new CloseChatRoomEvent(meeting.getId()));
                publishNotificationForCancelMeeting(meeting);
            } else {
                meeting.inProgress();
            }
            return meeting;
        };
    }

    private void publishNotificationForCancelMeeting(Meeting meeting) {
        List<MeetingParticipant> participants = meeting.getMeetingParticipants();

        for (MeetingParticipant participant : participants) {
            String message = String.format(MEETING_CANCEL, meeting.getName());
            NotificationEvent event = NotificationEvent.builder()
                    .userId(participant.getMember().getId().toString())
                    .message(message)
                    .type(NotificationType.CANCEL)
                    .relatedUrl("/" + meeting.getId())
                    .build();
            notificationEventPublisher.publishEvent(event);
            log.info("NotificationEvent published: {}", event);
        }
    }

    @Bean(UPDATE_MEETING_STATUS + "Writer")
    public ItemWriter<Meeting> writer() {
        return chunk -> {
            List<? extends Meeting> meetings = chunk.getItems();
            meetingRepository.saveAll(meetings);
        };
    }
}
