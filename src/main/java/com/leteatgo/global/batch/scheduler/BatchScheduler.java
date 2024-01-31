package com.leteatgo.global.batch.scheduler;

import com.leteatgo.global.batch.meeting.CancelUnmatchedMeetingsConfig;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final CancelUnmatchedMeetingsConfig cancelUnmatchedMeetingsConfig;

    @Scheduled(cron = "0 0 0/2 * * *")
    public void run() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addDate("date", new Date())
                .toJobParameters();

        try {
            log.info("BatchScheduler.run()");
            jobLauncher.run(cancelUnmatchedMeetingsConfig.job(), jobParameters);
        } catch (Exception e) {
            log.error("BatchScheduler.run() error", e);
        }
    }
}
