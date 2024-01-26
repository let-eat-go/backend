package com.leteatgo.domain.tastyrestaurant.repository;

import com.leteatgo.domain.tastyrestaurant.entity.TastyRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TastyRestaurantRepository extends JpaRepository<TastyRestaurant, Long>,
        CustomTastyRestaurantRepository {

}
