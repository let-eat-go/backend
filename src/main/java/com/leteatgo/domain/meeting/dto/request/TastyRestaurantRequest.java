package com.leteatgo.domain.meeting.dto.request;

import static com.leteatgo.global.constants.DtoValid.EMPTY_MESSAGE;

import jakarta.validation.constraints.NotNull;

public record TastyRestaurantRequest(
        String name,
        @NotNull(message = EMPTY_MESSAGE)
        String kakaoId,
        String category,
        String phoneNumber,
        String roadAddress,
        String landAddress,
        Double latitude,
        Double longitude,
        String restaurantUrl
) {

//    public static TastyRestaurantRequest toEntity(TastyRestaurantRequest request) {
//        return TastyRestaurantRequest.builder()
//                .name(request.name())
//                .category(request.category())
//                .phoneNumber(request.phoneNumber())
//                .roadAddress(request.roadAddress())
//                .landAddress(request.landAddress())
//                .latitude(request.latitude())
//                .longitude(request.longitude())
//                .restaurantUrl(request.restaurantUrl())
//                .build();
//    }
}
