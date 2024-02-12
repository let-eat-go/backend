package com.leteatgo.global.redis;

import static com.leteatgo.global.constants.RedisKey.KEYWORD_RANKING;
import static com.leteatgo.global.constants.RedisKey.TOP_COUNT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.leteatgo.global.config.RedisConfig;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.test.context.ActiveProfiles;

@Disabled
@ActiveProfiles("test")
@DataRedisTest
@Import(RedisConfig.class)
class RedisZSetOperTest {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    ZSetOperations<String, Object> zSetOperations;

    @BeforeEach
    void setup() {
        zSetOperations = redisTemplate.opsForZSet();
    }

    @AfterEach
    void teardown() {
        redisTemplate.delete(KEYWORD_RANKING);
    }

    @Test
    @DisplayName("redis increment 동시성 문제 발생 여부 확인")
    void saveSearchKeyword() throws InterruptedException {
        // given
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    zSetOperations.incrementScore(KEYWORD_RANKING, "keyword", 1);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        // then
        Set<TypedTuple<Object>> tuples = zSetOperations.reverseRangeWithScores(KEYWORD_RANKING,
                0, TOP_COUNT - 1);

        Double score = tuples.stream()
                .filter(o -> o.getValue().equals("keyword"))
                .findFirst().get().getScore();

        assertEquals(10.0, score);
    }
}