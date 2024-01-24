package com.leteatgo.domain.tastyrestaurant.service;

import com.leteatgo.domain.tastyrestaurant.dto.request.SearchRestaurantsRequest;
import com.leteatgo.domain.tastyrestaurant.dto.response.PopularKeywordsResponse;
import com.leteatgo.domain.tastyrestaurant.dto.response.PopularKeywordsResponse.Keywords;
import com.leteatgo.domain.tastyrestaurant.dto.response.SearchRestaurantsResponse;
import com.leteatgo.domain.tastyrestaurant.repository.TastyRestaurantRepository;
import com.leteatgo.global.external.searchplace.client.RestaurantSearcher;
import com.leteatgo.global.external.searchplace.dto.RestaurantsResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TastyRestaurantService {

    private final RestaurantSearcher RestaurantSearcher;
    private final TastyRestaurantRepository tastyRestaurantRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String KEYWORD_RANKING = "ranking:keyword";

    public SearchRestaurantsResponse searchRestaurants(SearchRestaurantsRequest request) {
        RestaurantsResponse response = RestaurantSearcher.searchRestaurants(
                request.keyword(),
                request.page(),
                request.longitude(),
                request.latitude(),
                request.radius(),
                request.sort());

        saveKeyword(request.keyword());
        return SearchRestaurantsResponse.from(response, request.page());
    }

    public void saveKeyword(String keyword) { // todo 최적화(스케줄링) -> ranking이 낮은 순으로 조회 후 삭제 (top5만 남긴다.)
        redisTemplate.opsForZSet().incrementScore(KEYWORD_RANKING, keyword, 1);
    }

    public PopularKeywordsResponse getKeywordRankingTop5() {
        return new PopularKeywordsResponse(Objects.requireNonNull(redisTemplate.opsForZSet()
                        .reverseRangeWithScores(KEYWORD_RANKING, 0, 4)).stream()
                .map(Keywords::of)
                .toList());
    }
}
