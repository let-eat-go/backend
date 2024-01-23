package com.leteatgo.domain.tastyrestaurant.service;

import com.leteatgo.domain.tastyrestaurant.dto.request.SearchRestaurantRequest;
import com.leteatgo.domain.tastyrestaurant.dto.response.SearchRestaurantResponse;
import com.leteatgo.domain.tastyrestaurant.repository.TastyRestaurantRepository;
import com.leteatgo.global.external.searchplace.client.SearchPlaceClient;
import com.leteatgo.global.external.searchplace.dto.SearchPlaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
@Service
public class TastyRestaurantService {

    private final SearchPlaceClient SearchPlaceClient;
    private final TastyRestaurantRepository tastyRestaurantRepository;

    public SearchRestaurantResponse searchRestaurants(SearchRestaurantRequest request) {
        SearchPlaceResponse response;

        if (!ObjectUtils.isEmpty(request.longitude()) && !ObjectUtils.isEmpty(request.latitude())) {
            response = SearchPlaceClient.searchPlaceWithDistance(
                    request.keyword(),
                    request.page(),
                    request.longitude(),
                    request.latitude(),
                    request.radius(),
                    request.sort());
        } else {
            response = SearchPlaceClient.searchPlace(request.keyword(), request.page());
        }

        return SearchRestaurantResponse.from(response, request.page());
    }
}
