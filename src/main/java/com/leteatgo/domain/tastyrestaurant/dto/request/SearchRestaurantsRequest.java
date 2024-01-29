package com.leteatgo.domain.tastyrestaurant.dto.request;

import static com.leteatgo.global.constants.DtoValid.EMPTY_KEYWORD;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.springframework.util.ObjectUtils;

@Builder
public record SearchRestaurantsRequest(
        @NotBlank(message = EMPTY_KEYWORD)
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

    public SearchRestaurantsRequest {
        if (ObjectUtils.isEmpty(page)) {
            page = 1;
        }
    }
}
