package com.leteatgo.global.batch.scheduler;

import com.leteatgo.global.batch.config.CancelUnmatchedMeetingsConfig;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // 10초 마다 실행
//    @Scheduled(cron = "0/10 * * * * *")
    public void run() {
        try {
            log.info("BatchScheduler.run()");
            jobLauncher.run(cancelUnmatchedMeetingsConfig.job(),
                    new JobParametersBuilder()
                            .addDate("date", new Date())
                            .toJobParameters());
        } catch (Exception e) {
            log.error("BatchScheduler.run() error", e);
        }
    }
}
