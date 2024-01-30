package com.leteatgo.domain.tastyrestaurant.repository;

import com.leteatgo.domain.tastyrestaurant.entity.TastyRestaurant;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TastyRestaurantRepository extends JpaRepository<TastyRestaurant, Long> {

    Slice<TastyRestaurant> findAllByOrderByNumberOfUsesDesc(Pageable pageable);

    Optional<TastyRestaurant> findByKakaoId(Long kakaoId);

}
