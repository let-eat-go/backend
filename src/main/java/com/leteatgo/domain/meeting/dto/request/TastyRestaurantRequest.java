package com.leteatgo.domain.meeting.dto.request;

import static com.leteatgo.global.constants.DtoValid.EMPTY_MESSAGE;

import com.leteatgo.domain.tastyrestaurant.entity.TastyRestaurant;
import com.leteatgo.global.type.RestaurantCategory;
import jakarta.validation.constraints.NotNull;

public record TastyRestaurantRequest(
        String name,
        @NotNull(message = EMPTY_MESSAGE)
        String apiId,
        String category,
        String phoneNumber,
        String roadAddress,
        String landAddress,
        Double latitude,
        Double longitude,
        String restaurantUrl
) {

    public static TastyRestaurant toEntity(TastyRestaurantRequest request) {
        return TastyRestaurant.builder()
                .apiId(Long.parseLong(request.apiId()))
                .name(request.name())
                .category(RestaurantCategory.from(request.category()))
                .phoneNumber(request.phoneNumber())
                .roadAddress(request.roadAddress())
                .landAddress(request.landAddress())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .restaurantUrl(request.restaurantUrl())
                .numberOfUses(1)
                .build();
    }
}
