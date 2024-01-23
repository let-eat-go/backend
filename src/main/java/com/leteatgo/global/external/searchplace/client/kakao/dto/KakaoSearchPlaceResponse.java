package com.leteatgo.global.external.searchplace.client.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leteatgo.global.external.searchplace.dto.RestaurantContent;
import com.leteatgo.global.external.searchplace.dto.RestaurantMeta;
import com.leteatgo.global.external.searchplace.dto.SearchPlaceResponse;
import java.util.List;

public record KakaoSearchPlaceResponse(List<Document> documents,
                                       Meta meta) implements SearchPlaceResponse {

    @Override
    public List<RestaurantContent> getContents() {
        if (documents.isEmpty()) {
            return List.of();
        }
        return documents.stream()
                .map(RestaurantContent::from)
                .toList();
    }

    @Override
    public RestaurantMeta getMeta() {
        return new RestaurantMeta(meta.hasNext(), meta.totalCount());
    }

    public record Document(
            @JsonProperty("id")
            Long kakaoId,

            @JsonProperty("place_name")
            String name,

            @JsonProperty("category_name")
            String category,

            @JsonProperty("phone")
            String phoneNumber,

            @JsonProperty("road_address_name")
            String roadAddress,

            @JsonProperty("address_name")
            String landAddress,

            @JsonProperty("y")
            Double latitude,

            @JsonProperty("x")
            Double longitude,

            @JsonProperty("place_url")
            String restaurantUrl
    ) {
    }

    public record Meta(@JsonProperty("is_end") Boolean hasNext,
                       @JsonProperty("total_count") Integer totalCount) {
    }
}
