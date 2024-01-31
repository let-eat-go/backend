package com.leteatgo.global.batch.meeting;

import static com.leteatgo.global.constants.BeanPrefix.CANCEL_UNMATCHED_MEETING;

import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.repository.MeetingRepository;
import com.leteatgo.domain.meeting.type.MeetingStatus;
import java.time.LocalDate;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
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
public class CancelUnmatchedMeetingsConfig extends DefaultBatchConfiguration {

    private final static Integer CHUNK_SIZE = 100;

    private final MeetingRepository meetingRepository;

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
    @StepScope
    public ItemReader<Meeting> reader() {
        return () -> {
            LocalDate nowDate = LocalDate.now();
            Iterator<Meeting> meetingIterator = meetingRepository.findMeetingsForCancel(
                    nowDate, MeetingStatus.BEFORE).iterator();

            if (meetingIterator.hasNext()) {
                return meetingIterator.next();
            } else {
                return null;
            }
        };
    }

    @Bean(CANCEL_UNMATCHED_MEETING + "Processor")
    public ItemProcessor<Meeting, Meeting> processor() {
        return meeting -> {
            meeting.cancel();
            // TODO: 취소 알림
            return meeting;
        };
    }

    @Bean(CANCEL_UNMATCHED_MEETING + "Writer")
    public ItemWriter<Meeting> writer() {
        return meetingRepository::saveAll;
    }
}
