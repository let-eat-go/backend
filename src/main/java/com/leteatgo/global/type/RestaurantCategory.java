package com.leteatgo.global.type;

import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_CATEGORY;

import com.leteatgo.domain.tastyrestaurant.exception.TastyRestaurantException;
import java.util.Arrays;

public enum RestaurantCategory {
    간식, 분식, 뷔페, 술집, 아시아음식, 양식, 일식, 중식, 패스트푸드, 패밀리레스토랑, 피자, 치킨, 한식;

    public static RestaurantCategory from(String category) {
        return Arrays.stream(values())
                .filter(o -> o.name().equals(category))
                .findFirst()
                .orElseThrow(() -> new TastyRestaurantException(NOT_FOUND_CATEGORY));
    }
}
