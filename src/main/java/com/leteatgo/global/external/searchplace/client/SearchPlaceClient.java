package com.leteatgo.global.external.searchplace.client;

import com.leteatgo.global.external.searchplace.dto.SearchPlaceResponse;

public interface SearchPlaceClient {

    SearchPlaceResponse searchPlace(String keyword, Integer page);

    SearchPlaceResponse searchPlaceWithDistance(String keyword, Integer page,
            Double longitude, Double latitude, Integer radius, String sort);
}
