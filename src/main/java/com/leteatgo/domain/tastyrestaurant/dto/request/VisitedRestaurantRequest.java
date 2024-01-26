package com.leteatgo.domain.tastyrestaurant.dto.request;

public record VisitedRestaurantRequest(
        Integer page,
        Integer lastNum
) {

    public VisitedRestaurantRequest {
        page -= 1; // request page start : 1
    }
}
