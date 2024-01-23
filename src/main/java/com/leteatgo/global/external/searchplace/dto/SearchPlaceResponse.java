package com.leteatgo.global.external.searchplace.dto;

import java.util.List;

public interface SearchPlaceResponse {
    List<RestaurantContent> getContents();

    RestaurantMeta getMeta();
}
