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
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/tasty-restaurants")
@RequiredArgsConstructor
@RestController
public class TastyRestaurantController {

    private final TastyRestaurantService tastyRestaurantService;

    /**
     * 맛집 검색
     *
     * @param request 요청 파라미터
     * @return 맛집 리스트
     */
    @RoleUser
    @GetMapping("/search")
    public ResponseEntity<SearchRestaurantsResponse> searchRestaurants(
            @Valid SearchRestaurantsRequest request) {
        return ResponseEntity.ok(tastyRestaurantService.searchRestaurants(request));
    }

    /**
     * 인기 검색어
     *
     * @return 인기 검색어 리스트
     */
    @RoleUser
    @GetMapping("/popular")
    public ResponseEntity<PopularKeywordsResponse> popularKeywords() {
        return ResponseEntity.ok(tastyRestaurantService.getKeywordRanking());
    }

    /**
     * 회원들이 방문한 맛집 조회 (5개 반환)
     *
     * @return 방문한 맛집 리스트
     */
    @GetMapping
    public ResponseEntity<VisitedRestaurantResponse> visitedRestaurants() {
        return ResponseEntity.ok(tastyRestaurantService.visitedRestaurants());
    }
}
