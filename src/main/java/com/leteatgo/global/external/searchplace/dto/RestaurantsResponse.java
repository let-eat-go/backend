package com.leteatgo.global.external.searchplace.dto;

import java.util.List;

public interface RestaurantsResponse {
    List<RestaurantContent> getContents();

    RestaurantMeta getMeta();
}
