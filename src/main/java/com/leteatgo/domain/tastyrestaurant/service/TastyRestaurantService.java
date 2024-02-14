package com.leteatgo.domain.tastyrestaurant.service;

import com.leteatgo.domain.tastyrestaurant.dto.request.SearchRestaurantsRequest;
import com.leteatgo.domain.tastyrestaurant.dto.response.PopularKeywordsResponse;
import com.leteatgo.domain.tastyrestaurant.dto.response.SearchRestaurantsResponse;
import com.leteatgo.domain.tastyrestaurant.dto.response.VisitedRestaurantResponse;
import com.leteatgo.domain.tastyrestaurant.dto.response.VisitedRestaurantResponse.Content;
import com.leteatgo.domain.tastyrestaurant.repository.TastyRestaurantRepository;
import com.leteatgo.global.dto.CustomPageRequest;
import com.leteatgo.global.external.searchplace.client.RestaurantSearcher;
import com.leteatgo.global.external.searchplace.dto.RestaurantsResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TastyRestaurantService {

    private final RestaurantSearcher RestaurantSearcher;
    private final TastyRestaurantRepository tastyRestaurantRepository;
    private final RedisRankingService redisRankingService;

    private static final String CB_SEARCH_RESTAURANTS = "searchRestaurants";

    @CircuitBreaker(name = CB_SEARCH_RESTAURANTS, fallbackMethod = "searchRestaurantsFallback")
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

    public SearchRestaurantsResponse searchRestaurantsFallback(SearchRestaurantsRequest request,
            CallNotPermittedException exception) {
        log.info("fail searchRestaurants cause [{}: {}]", exception.getClass(),
                exception.getMessage());

        return SearchRestaurantsResponse.fromEntity(
                tastyRestaurantRepository.searchRestaurants(request,
                        PageRequest.of(request.page() - 1, CustomPageRequest.PAGE_SIZE)));
    }

    public PopularKeywordsResponse getKeywordRanking() {
        return redisRankingService.getKeywordRanking();
    }

    public VisitedRestaurantResponse visitedRestaurants() {
        return new VisitedRestaurantResponse(tastyRestaurantRepository
                .findTop5ByOrderByNumberOfUsesDesc().stream()
                .map(Content::fromEntity)
                .toList());
    }
}
