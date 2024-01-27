package com.leteatgo.domain.tastyrestaurant.dto.request;

import jakarta.annotation.Nullable;
import org.springframework.util.ObjectUtils;

public record VisitedRestaurantRequest(
        @Nullable
        Integer page
) {

    public VisitedRestaurantRequest {
        if (ObjectUtils.isEmpty(page)) {
            page = 0;
        } else {
            page -= 1; // request page start 1
        }
    }
}
