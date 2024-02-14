package com.leteatgo.domain.tastyrestaurant.dto.response;

import com.leteatgo.domain.tastyrestaurant.entity.TastyRestaurant;
import com.leteatgo.global.external.searchplace.dto.RestaurantContent;
import com.leteatgo.global.external.searchplace.dto.RestaurantsResponse;
import com.leteatgo.global.type.RestaurantCategory;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Slice;

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

    public static SearchRestaurantsResponse fromEntity(Slice<TastyRestaurant> slice) {
        return new SearchRestaurantsResponse(
                slice.getContent().stream()
                        .map(Content::fromEntity)
                        .toList(),
                Pagination.builder()
                        .currentPage(slice.getPageable().getPageNumber() + 1)
                        .hasMore(slice.hasNext())
                        .totalCount(-1) // slice에는 total이 없음
                        .build());
    }

    @Builder
    public record Content(
            Long apiId,
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
                    .apiId(content.apiId())
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

        public static Content fromEntity(TastyRestaurant tastyRestaurant) {
            return Content.builder()
                    .apiId(tastyRestaurant.getApiId())
                    .name(tastyRestaurant.getName())
                    .category(tastyRestaurant.getCategory())
                    .phoneNumber(tastyRestaurant.getPhoneNumber())
                    .roadAddress(tastyRestaurant.getRoadAddress())
                    .landAddress(tastyRestaurant.getLandAddress())
                    .latitude(tastyRestaurant.getLatitude())
                    .longitude(tastyRestaurant.getLongitude())
                    .restaurantUrl(tastyRestaurant.getRestaurantUrl())
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
