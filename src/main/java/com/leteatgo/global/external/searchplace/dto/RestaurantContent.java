package com.leteatgo.global.external.searchplace.dto;

import com.leteatgo.global.external.searchplace.client.kakao.dto.KakaoSearchPlaceResponse;
import lombok.Builder;

@Builder
public record RestaurantContent(
        Long id,
        String name,
        String category,
        String phoneNumber,
        String roadAddress,
        String landAddress,
        Double latitude,
        Double longitude,
        String restaurantUrl
) {

    public static RestaurantContent from(KakaoSearchPlaceResponse.Document document) {
        return RestaurantContent.builder()
                .id(document.kakaoId())
                .name(document.name())
                .category(document.category().split(">")[1].trim())
                .phoneNumber(document.phoneNumber())
                .roadAddress(document.roadAddress())
                .landAddress(document.landAddress())
                .latitude(document.latitude())
                .longitude(document.longitude())
                .restaurantUrl(document.restaurantUrl())
                .build();
    }
}
