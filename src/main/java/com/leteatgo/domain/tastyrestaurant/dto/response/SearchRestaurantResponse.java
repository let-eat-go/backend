package com.leteatgo.domain.tastyrestaurant.dto.response;

import com.leteatgo.global.external.searchplace.dto.RestaurantContent;
import com.leteatgo.global.external.searchplace.dto.SearchPlaceResponse;
import java.util.List;
import lombok.Builder;

public record SearchRestaurantResponse(
        List<Content> contents,
        Pagination pagination
) {

    public static SearchRestaurantResponse from(SearchPlaceResponse response,
            Integer currentPage) {
        return new SearchRestaurantResponse(
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
            String category,
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
                    .category(content.category())
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
    public record Pagination(Integer currentPage,
                             boolean hasMore,
                             Integer totalCount) {
    }
}