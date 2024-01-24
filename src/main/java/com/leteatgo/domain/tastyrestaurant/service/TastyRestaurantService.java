package com.leteatgo.domain.tastyrestaurant.service;

import com.leteatgo.domain.tastyrestaurant.dto.request.SearchRestaurantsRequest;
import com.leteatgo.domain.tastyrestaurant.dto.response.PopularKeywordsResponse;
import com.leteatgo.domain.tastyrestaurant.dto.response.SearchRestaurantsResponse;
import com.leteatgo.domain.tastyrestaurant.repository.TastyRestaurantRepository;
import com.leteatgo.global.external.searchplace.client.RestaurantSearcher;
import com.leteatgo.global.external.searchplace.dto.RestaurantsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TastyRestaurantService {

    private final RestaurantSearcher RestaurantSearcher;
    private final TastyRestaurantRepository tastyRestaurantRepository;
    private final RedisRankingService redisRankingService;

    public SearchRestaurantsResponse searchRestaurants(SearchRestaurantsRequest request) {
        RestaurantsResponse response = RestaurantSearcher.searchRestaurants(
                request.keyword(),
                request.page(),
                request.longitude(),
                request.latitude(),
                request.radius(),
                request.sort());

        redisRankingService.saveSearchKeyword(request.keyword());
        return SearchRestaurantsResponse.from(response, request.page());
    }

    public PopularKeywordsResponse getKeywordRanking() {
        return redisRankingService.getKeywordRanking();
    }
}
