package com.leteatgo.domain.tastyrestaurant.service;

import static com.leteatgo.global.constants.RedisKey.KEYWORD_RANKING;
import static com.leteatgo.global.constants.RedisKey.TOP_COUNT;

import com.leteatgo.domain.tastyrestaurant.dto.response.PopularKeywordsResponse;
import com.leteatgo.domain.tastyrestaurant.dto.response.PopularKeywordsResponse.Keywords;
import java.util.Objects;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisRankingService {

    private final ZSetOperations<String, Object> zSetOperations;

    public RedisRankingService(RedisTemplate<String, Object> redisTemplate) {
        zSetOperations = redisTemplate.opsForZSet();
    }

    public void saveSearchKeyword(String keyword) {
        zSetOperations.incrementScore(KEYWORD_RANKING, keyword, 1);
    }

    public PopularKeywordsResponse getKeywordRanking() {
        return new PopularKeywordsResponse(Objects.requireNonNull(zSetOperations
                        .reverseRangeWithScores(KEYWORD_RANKING, 0, TOP_COUNT - 1))
                .stream()
                .map(Keywords::from)
                .toList());
    }
}
