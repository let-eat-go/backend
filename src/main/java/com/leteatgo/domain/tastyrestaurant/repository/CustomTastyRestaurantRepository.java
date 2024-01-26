package com.leteatgo.domain.tastyrestaurant.repository;

import com.leteatgo.domain.tastyrestaurant.entity.TastyRestaurant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomTastyRestaurantRepository {

    Slice<TastyRestaurant> visitedRestaurants(Integer lastNumOfUses, Pageable pageable);
}
