package com.leteatgo.global.batch.config;

import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.repository.MeetingRepository;
import com.leteatgo.global.batch.job.UnmatchedMeetingItemProcessor;
import com.leteatgo.global.batch.job.UnmatchedMeetingItemReader;
import com.leteatgo.global.batch.job.UnmatchedMeetingItemWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class CancelUnmatchedMeetingsConfig extends BatchConfig {

    private final MeetingRepository meetingRepository;


    @Override
    protected Step step() {
        return new StepBuilder("cancelUnmatchedMeetingsStep", jobRepository())
                .<Meeting, Meeting>chunk(100, getTransactionManager())
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public UnmatchedMeetingItemReader reader() {
        return new UnmatchedMeetingItemReader(meetingRepository);
    }

    @Bean
    public UnmatchedMeetingItemProcessor processor() {
        return new UnmatchedMeetingItemProcessor();
    }

    @Bean
    public UnmatchedMeetingItemWriter writer() {
        return new UnmatchedMeetingItemWriter(meetingRepository);
    }

}
