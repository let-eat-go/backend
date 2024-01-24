package com.leteatgo.global.external.searchplace.client.kakao.client;

import com.leteatgo.global.external.searchplace.client.SearchRestaurantClient;
import com.leteatgo.global.external.searchplace.client.kakao.dto.KakaoRestaurantsResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface KakaoApiClient extends SearchRestaurantClient {

    @GetExchange
    @Override
    KakaoRestaurantsResponse searchRestaurants(
            @RequestParam("query") String keyword,
            @RequestParam("page") Integer page,
            @RequestParam(value = "x", required = false) Double longitude,
            @RequestParam(value = "y", required = false) Double latitude,
            @RequestParam(value = "radius", required = false) Integer radius,
            @RequestParam(value = "sort", required = false) String sort
    );
}
