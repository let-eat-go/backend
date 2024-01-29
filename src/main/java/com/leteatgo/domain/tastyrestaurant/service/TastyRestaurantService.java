package com.leteatgo.domain.tastyrestaurant.service;

import com.leteatgo.domain.tastyrestaurant.dto.request.SearchRestaurantsRequest;
import com.leteatgo.domain.tastyrestaurant.dto.request.VisitedRestaurantRequest;
import com.leteatgo.domain.tastyrestaurant.dto.response.PopularKeywordsResponse;
import com.leteatgo.domain.tastyrestaurant.dto.response.SearchRestaurantsResponse;
import com.leteatgo.domain.tastyrestaurant.dto.response.VisitedRestaurantResponse;
import com.leteatgo.domain.tastyrestaurant.dto.response.VisitedRestaurantResponse.Content;
import com.leteatgo.domain.tastyrestaurant.dto.response.VisitedRestaurantResponse.Pagination;
import com.leteatgo.domain.tastyrestaurant.entity.TastyRestaurant;
import com.leteatgo.domain.tastyrestaurant.repository.TastyRestaurantRepository;
import com.leteatgo.global.external.searchplace.client.RestaurantSearcher;
import com.leteatgo.global.external.searchplace.dto.RestaurantsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TastyRestaurantService {

    private final RestaurantSearcher RestaurantSearcher;
    private final TastyRestaurantRepository tastyRestaurantRepository;
    private final RedisRankingService redisRankingService;

    private static final Integer VISITED_PAGE_SIZE = 10;

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

    public VisitedRestaurantResponse visitedRestaurants(VisitedRestaurantRequest request) {
        Slice<TastyRestaurant> tastyRestaurants = tastyRestaurantRepository
                .findAllByOrderByNumberOfUsesDesc(PageRequest.of(request.page(), VISITED_PAGE_SIZE));

        return new VisitedRestaurantResponse(
                tastyRestaurants.getContent().stream()
                        .map(Content::fromEntity)
                        .toList(),
                new Pagination(tastyRestaurants.getPageable().getPageNumber() + 1,
                        tastyRestaurants.hasNext()));
    }
}
