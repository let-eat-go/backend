package com.leteatgo.domain.tastyrestaurant.dto.response;

import com.leteatgo.global.external.searchplace.dto.RestaurantContent;
import com.leteatgo.global.external.searchplace.dto.RestaurantsResponse;
import com.leteatgo.global.type.RestaurantCategory;
import java.util.List;
import lombok.Builder;

public record SearchRestaurantsResponse(
        List<Content> contents,
        Pagination pagination
) {

    public static SearchRestaurantsResponse from(RestaurantsResponse response,
            Integer currentPage) {
        return new SearchRestaurantsResponse(
                response.getContents().stream()
                        .map(Content::from)
                        .toList(),
                Pagination.builder()
                        .currentPage(currentPage)
                        .hasMore(response.getMeta().hasNext())
                        .totalCount(response.getMeta().totalCount())
                        .build());
    }

    @Builder
    public record Content(
            String name,
            RestaurantCategory category,
            String phoneNumber,
            String roadAddress,
            String landAddress,
            Double latitude,
            Double longitude,
            String restaurantUrl
    ) {

        public static Content from(RestaurantContent content) {
            return Content.builder()
                    .name(content.name())
                    .category(RestaurantCategory.from(content.category()))
                    .phoneNumber(content.phoneNumber())
                    .roadAddress(content.roadAddress())
                    .landAddress(content.landAddress())
                    .latitude(content.latitude())
                    .longitude(content.longitude())
                    .restaurantUrl(content.restaurantUrl())
                    .build();
        }
    }

    @Builder
    public record Pagination(
            Integer currentPage,
            boolean hasMore,
            Integer totalCount
    ) {

    }
}
