package com.leteatgo.domain.tastyrestaurant.repository;

import com.leteatgo.domain.tastyrestaurant.entity.TastyRestaurant;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TastyRestaurantRepository extends JpaRepository<TastyRestaurant, Long> {

    List<TastyRestaurant> findTop5ByOrderByNumberOfUsesDesc();

    Optional<TastyRestaurant> findByApiId(Long kakaoId);

}
