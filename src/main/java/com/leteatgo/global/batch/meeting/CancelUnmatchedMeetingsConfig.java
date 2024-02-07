package com.leteatgo.global.batch.meeting;

import static com.leteatgo.global.constants.BeanPrefix.CANCEL_UNMATCHED_MEETING;
import static com.leteatgo.global.constants.NotificationMessage.MEETING_CANCEL;

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
public class CancelUnmatchedMeetingsConfig extends DefaultBatchConfiguration {

    private final static Integer CHUNK_SIZE = 100;

    private final MeetingRepository meetingRepository;
    private final ChatRoomEventPublisher chatRoomEventPublisher;
    private final NotificationEventPublisher notificationEventPublisher;

    @Bean(CANCEL_UNMATCHED_MEETING + "Job")
    public Job job() {
        return new JobBuilder(CANCEL_UNMATCHED_MEETING + "Job", jobRepository())
                .incrementer(new RunIdIncrementer())
                .start(cancelMeetingStep())
                .build();
    }

    @Bean(CANCEL_UNMATCHED_MEETING + "Step")
    @JobScope
    public Step cancelMeetingStep() {
        return new StepBuilder(CANCEL_UNMATCHED_MEETING + "Step", jobRepository())
                .<Meeting, Meeting>chunk(CHUNK_SIZE, getTransactionManager())
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean(CANCEL_UNMATCHED_MEETING + "Reader")
    public ItemReader<Meeting> reader() {
        return new ItemReader<>() {
            private Iterator<Meeting> meetingIterator;

            @Override
            public Meeting read() {
                if (meetingIterator == null) {
                    LocalDateTime startDateTime = LocalDateTime.now();
                    meetingIterator = meetingRepository.findMeetingsForCancel(
                            startDateTime, MeetingStatus.BEFORE).iterator();
                }

                if (meetingIterator.hasNext()) {
                    return meetingIterator.next();
                } else {
                    return null;
                }
            }
        };
    }

    @Bean(CANCEL_UNMATCHED_MEETING + "Processor")
    public ItemProcessor<Meeting, Meeting> processor() {
        return meeting -> {
            meeting.cancel();
            chatRoomEventPublisher.publishCloseChatRoom(new CloseChatRoomEvent(meeting.getId()));
            publishNotificationForCancelMeeting(meeting);
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
                    .relatedUrl("/api/meetings/detail/" + meeting.getId()) // TODO: 프론트 URL로 변경
                    .build();
            notificationEventPublisher.publishEvent(event);
            log.info("NotificationEvent published: {}", event);
        }
    }

    @Bean(CANCEL_UNMATCHED_MEETING + "Writer")
    public ItemWriter<Meeting> writer() {
        return chunk -> {
            List<? extends Meeting> meetings = chunk.getItems();
            meetingRepository.saveAll(meetings);
        };
    }
}
