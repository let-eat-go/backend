package com.leteatgo.domain.tastyrestaurant.controller;

import com.leteatgo.domain.tastyrestaurant.dto.request.SearchRestaurantsRequest;
import com.leteatgo.domain.tastyrestaurant.dto.response.PopularKeywordsResponse;
import com.leteatgo.domain.tastyrestaurant.dto.response.SearchRestaurantsResponse;
import com.leteatgo.domain.tastyrestaurant.dto.response.VisitedRestaurantResponse;
import com.leteatgo.domain.tastyrestaurant.service.TastyRestaurantService;
import com.leteatgo.global.security.annotation.RoleUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/tasty-restaurants")
@RequiredArgsConstructor
@RestController
public class TastyRestaurantController {

    private final TastyRestaurantService tastyRestaurantService;

    @RoleUser
    @GetMapping("/search")
    public ResponseEntity<SearchRestaurantsResponse> searchRestaurants(
            @Valid SearchRestaurantsRequest request) {
        return ResponseEntity.ok(tastyRestaurantService.searchRestaurants(request));
    }

    @RoleUser
    @GetMapping("/popular")
    public ResponseEntity<PopularKeywordsResponse> popularKeywords() {
        return ResponseEntity.ok(tastyRestaurantService.getKeywordRanking());
    }

    @GetMapping
    public ResponseEntity<VisitedRestaurantResponse> visitedRestaurants(
            @RequestParam(value = "lastNumOfUses", required = false) Integer lastNumOfUses) {
        return ResponseEntity.ok(tastyRestaurantService.visitedRestaurants(lastNumOfUses));
    }
}
