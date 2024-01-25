package com.leteatgo.global.external.searchplace.client;

import com.leteatgo.global.external.searchplace.dto.RestaurantsResponse;

public interface RestaurantSearcher {

    RestaurantsResponse searchRestaurants(String keyword, Integer page,
            Double longitude, Double latitude, Integer radius, String sort);
}
