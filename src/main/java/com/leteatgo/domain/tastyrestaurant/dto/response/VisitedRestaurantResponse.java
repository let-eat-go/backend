package com.leteatgo.domain.tastyrestaurant.dto.response;

import com.leteatgo.domain.tastyrestaurant.entity.TastyRestaurant;
import com.leteatgo.global.type.RestaurantCategory;
import java.util.List;
import lombok.Builder;

public record VisitedRestaurantResponse(
        List<Content> contents,
        Pagination pagination
) {

    @Builder
    public record Content(
            String name,
            RestaurantCategory category,
            String phoneNumber,
            String roadAddress,
            String landAddress,
            Double latitude,
            Double longitude,
            String restaurantUrl,
            Integer numberOfUses
    ) {

        public static Content fromEntity(TastyRestaurant tastyRestaurant) {
            return Content.builder()
                    .name(tastyRestaurant.getName())
                    .category(tastyRestaurant.getCategory())
                    .phoneNumber(tastyRestaurant.getPhoneNumber())
                    .roadAddress(tastyRestaurant.getRoadAddress())
                    .landAddress(tastyRestaurant.getLandAddress())
                    .latitude(tastyRestaurant.getLatitude())
                    .longitude(tastyRestaurant.getLongitude())
                    .restaurantUrl(tastyRestaurant.getRestaurantUrl())
                    .numberOfUses(tastyRestaurant.getNumberOfUses())
                    .build();
        }

    }

    public record Pagination(
            Integer lastNumOfUses,
            boolean hasMore
    ) {

    }
}
