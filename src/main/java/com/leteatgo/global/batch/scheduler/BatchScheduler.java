package com.leteatgo.global.batch.scheduler;

import com.leteatgo.global.batch.meeting.CompleteMeetingsConfig;
import com.leteatgo.global.batch.meeting.RemindMeetingsBeforeOneDayConfig;
import com.leteatgo.global.batch.meeting.RemindUpcomingMeetingsConfig;
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
    private final CompleteMeetingsConfig completeMeetingsConfig;
    private final RemindMeetingsBeforeOneDayConfig remindMeetingsBeforeOneDayConfig;
    private final RemindUpcomingMeetingsConfig remindUpcomingMeetingsConfig;

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

    // 매일 자정에 실행
//    @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0/30 * * * * *")
    public void runCompleteMeetingsJob() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addDate("date", new Date())
                .toJobParameters();

        try {
            log.info("CompleteMeetingsConfig.run() start");
            jobLauncher.run(completeMeetingsConfig.job(), jobParameters);
        } catch (Exception e) {
            log.error("CompleteMeetingsConfig.run() error", e);
        }
    }

    // 매일 저녁 8시에 실행
    @Scheduled(cron = "0 0 20 * * *")
    public void runRemindMeetingsBeforeOneDayJob() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addDate("date", new Date())
                .toJobParameters();

        try {
            log.info("RemindMeetingsConfig.run() start");
            jobLauncher.run(remindMeetingsBeforeOneDayConfig.job(), jobParameters);
        } catch (Exception e) {
            log.error("RemindMeetingsConfig.run() error", e);
        }
    }

    // 한시간마다 실행
    @Scheduled(cron = "0 0 0/1 * * *")
    public void runUpcomingMeetingsNotificationJob() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addDate("date", new Date())
                .toJobParameters();

        try {
            log.info("UpcomingMeetingsNotificationConfig.run() start");
            jobLauncher.run(remindUpcomingMeetingsConfig.job(), jobParameters);
        } catch (Exception e) {
            log.error("UpcomingMeetingsNotificationConfig.run() error", e);
        }
    }
}
