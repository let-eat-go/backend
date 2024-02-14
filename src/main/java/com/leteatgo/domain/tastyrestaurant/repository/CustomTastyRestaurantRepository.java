package com.leteatgo.domain.tastyrestaurant.repository;

import com.leteatgo.domain.tastyrestaurant.dto.request.SearchRestaurantsRequest;
import com.leteatgo.domain.tastyrestaurant.entity.TastyRestaurant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomTastyRestaurantRepository {

    Slice<TastyRestaurant> searchRestaurants(SearchRestaurantsRequest request, Pageable pageable);
}
