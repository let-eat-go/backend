package com.leteatgo.domain.tastyrestaurant.controller;

import com.leteatgo.domain.tastyrestaurant.dto.request.SearchRestaurantsRequest;
import com.leteatgo.domain.tastyrestaurant.dto.response.PopularKeywordsResponse;
import com.leteatgo.domain.tastyrestaurant.dto.response.SearchRestaurantsResponse;
import com.leteatgo.domain.tastyrestaurant.service.TastyRestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/tasty-restaurants")
@RequiredArgsConstructor
@RestController
public class TastyRestaurantController {

    private final TastyRestaurantService tastyRestaurantService;

    @GetMapping("/search")
    public ResponseEntity<SearchRestaurantsResponse> searchRestaurants(
            @Valid SearchRestaurantsRequest request) {
        return ResponseEntity.ok(tastyRestaurantService.searchRestaurants(request));
    }

    @GetMapping("/popular")
    public ResponseEntity<PopularKeywordsResponse> popularKeywords() {
        return ResponseEntity.ok(tastyRestaurantService.getKeywordRankingTop5());
    }
}
