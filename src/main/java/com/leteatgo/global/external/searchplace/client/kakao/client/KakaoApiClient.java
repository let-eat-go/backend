package com.leteatgo.global.external.searchplace.client.kakao.client;

import com.leteatgo.global.external.searchplace.client.RestaurantSearcher;
import com.leteatgo.global.external.searchplace.client.kakao.dto.KakaoRestaurantsResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface KakaoApiClient extends RestaurantSearcher {

    @Override
    @GetExchange("/keyword.json?category_group_code=FD6&size=10")
    KakaoRestaurantsResponse searchRestaurants(
            @RequestParam("query") String keyword,
            @RequestParam("page") Integer page,
            @RequestParam(value = "x", required = false) Double longitude,
            @RequestParam(value = "y", required = false) Double latitude,
            @RequestParam(value = "radius", required = false) Integer radius,
            @RequestParam(value = "sort", required = false) String sort
    );
}
