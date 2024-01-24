package com.leteatgo.domain.tastyrestaurant.scheduler;

import com.leteatgo.domain.tastyrestaurant.service.RedisRankingService;
import com.leteatgo.global.constants.RedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TastyRestaurantScheduler {

    private final RedisRankingService redisRankingService;

    @Scheduled(cron = "${scheduler.keyword-ranking}")
    public void deleteNonRankingKeywords() {
        long start = System.currentTimeMillis();
        log.info("[scheduling] start deleting keywords excluding top keywords");

        Long totalCount = redisRankingService.getTotalCount();

        if (totalCount != null && totalCount > RedisKey.TOP_COUNT) {
            redisRankingService.deleteAllExcludingTopKeywords(totalCount);
            long end = System.currentTimeMillis();
            log.info("[scheduling] finish deleting keywords. {} ms", end - start);
        } else {
            log.info("[scheduling] no keywords to delete");
        }
    }
}
