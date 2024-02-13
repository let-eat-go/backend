package com.leteatgo.global.batch.scheduler;

import com.leteatgo.global.batch.meeting.UpdateMeetingsStatusConfig;
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
    private final UpdateMeetingsStatusConfig updateMeetingsStatusConfig;

    // 30분마다 실행
    @Scheduled(cron = "0 0/30 * * * *")
    public void runUpdateMeetingsStatusJob() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addDate("date", new Date())
                .toJobParameters();

        try {
            log.info("UpdateMeetingsStatusConfig.run() start");
            jobLauncher.run(updateMeetingsStatusConfig.job(), jobParameters);
        } catch (Exception e) {
            log.error("UpdateMeetingsStatusConfig.run() error", e);
        }
    }
}
