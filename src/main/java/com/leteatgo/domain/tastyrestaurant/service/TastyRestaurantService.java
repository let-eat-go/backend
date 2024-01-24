package com.leteatgo.domain.tastyrestaurant.service;

import com.leteatgo.domain.tastyrestaurant.dto.request.SearchRestaurantsRequest;
import com.leteatgo.domain.tastyrestaurant.dto.response.SearchRestaurantsResponse;
import com.leteatgo.domain.tastyrestaurant.repository.TastyRestaurantRepository;
import com.leteatgo.global.external.searchplace.client.SearchRestaurantClient;
import com.leteatgo.global.external.searchplace.dto.RestaurantsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TastyRestaurantService {

    private final SearchRestaurantClient SearchRestaurantClient;
    private final TastyRestaurantRepository tastyRestaurantRepository;

    public SearchRestaurantsResponse searchRestaurants(SearchRestaurantsRequest request) {
        RestaurantsResponse response = SearchRestaurantClient.searchRestaurants(
                request.keyword(),
                request.page(),
                request.longitude(),
                request.latitude(),
                request.radius(),
                request.sort());

        return SearchRestaurantsResponse.from(response, request.page());
    }
}
