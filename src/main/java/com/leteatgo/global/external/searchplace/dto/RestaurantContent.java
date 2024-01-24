package com.leteatgo.global.external.searchplace.dto;

import com.leteatgo.global.external.searchplace.client.kakao.dto.KakaoRestaurantsResponse;
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

    public static RestaurantContent from(KakaoRestaurantsResponse.Document document) {
        String[] categories = document.category().split(">");
        String category = categories.length > 1 ? categories[1].trim() : categories[0].trim();

        return RestaurantContent.builder()
                .id(document.kakaoId())
                .name(document.name())
                .category(category)
                .phoneNumber(document.phoneNumber())
                .roadAddress(document.roadAddress())
                .landAddress(document.landAddress())
                .latitude(document.latitude())
                .longitude(document.longitude())
                .restaurantUrl(document.restaurantUrl())
                .build();
    }
}
