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

    // 매일 오후 11시 50분에 실행 (startDate로 조회하기 때문에 자정 전에 실행해야 함) -> 싱크를 완벽히 맞출 수 있을까??
    @Scheduled(cron = "0 50 23 * * *")
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
