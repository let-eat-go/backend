package com.leteatgo.global.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public abstract class BatchConfig extends DefaultBatchConfiguration {

    protected abstract Step step();

    @Bean
    public Job job() {
        return new JobBuilder("job", jobRepository())
                .incrementer(new RunIdIncrementer())
                .start(step())
                .build();
    }
}
