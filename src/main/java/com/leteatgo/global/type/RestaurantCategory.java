package com.leteatgo.global.type;

import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_CATEGORY;

import com.leteatgo.domain.tastyrestaurant.exception.TastyRestaurantException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RestaurantCategory {
    SNACK("간식"),
    STREET_FOOD("분식"),
    BUFFET("뷔페"),
    PUB("술집"),
    ASIAN_CUISINE("아시아음식"),
    WESTERN_CUISINE("양식"),
    JAPANESE_CUISINE("일식"),
    CHINESE_CUISINE("중식"),
    FAST_FOOD("패스트푸드"),
    FAMILY_RESTAURANT("패밀리레스토랑"),
    PIZZA("피자"),
    CHICKEN("치킨"),
    KOREAN_CUISINE("한식");

    private final String description;

    public static RestaurantCategory from(String category) {
        return Arrays.stream(values())
                .filter(o -> o.getDescription().equals(category))
                .findFirst()
                .orElseThrow(() -> new TastyRestaurantException(NOT_FOUND_CATEGORY));
    }
}
