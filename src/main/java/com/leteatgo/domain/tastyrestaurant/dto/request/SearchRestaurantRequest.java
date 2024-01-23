package com.leteatgo.domain.tastyrestaurant.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public record SearchRestaurantRequest(
        @NotBlank
        String keyword,

        @Min(1) @Max(45)
        @Nullable
        Integer page,

        @Nullable
        Double latitude,

        @Nullable
        Double longitude,

        @Nullable
        Integer radius,

        @Nullable
        String sort
) {

    public SearchRestaurantRequest {
        if (ObjectUtils.isEmpty(page)) {
            page = 1;
        }

        if (!ObjectUtils.isEmpty(longitude) && !ObjectUtils.isEmpty(latitude)) {
            if (ObjectUtils.isEmpty(radius)) {
                radius = 1000;
            }

            if (!StringUtils.hasText(sort)) {
                sort = "accuracy";
            }
        }
    }
}
