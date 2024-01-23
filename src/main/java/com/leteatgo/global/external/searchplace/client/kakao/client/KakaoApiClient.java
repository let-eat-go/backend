package com.leteatgo.global.external.searchplace.client.kakao.client;

import com.leteatgo.global.external.searchplace.client.SearchPlaceClient;
import com.leteatgo.global.external.searchplace.client.kakao.dto.KakaoSearchPlaceResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface KakaoApiClient extends SearchPlaceClient {

    @GetExchange
    @Override
    KakaoSearchPlaceResponse searchPlace(@RequestParam("query") String keyword,
            @RequestParam("page") Integer page);

    @GetExchange
    @Override
    KakaoSearchPlaceResponse searchPlaceWithDistance(
            @RequestParam("query") String keyword,
            @RequestParam("page") Integer page,
            @RequestParam("x") Double longitude,
            @RequestParam("y") Double latitude,
            @RequestParam(value = "radius", required = false) Integer radius,
            @RequestParam(value = "sort", required = false) String sort
    );
}
