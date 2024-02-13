package com.leteatgo.domain.meeting.type;

import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_SEARCH_TYPE;

import com.leteatgo.domain.meeting.exception.MeetingException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchType {
    REGION("지역"),
    CATEGORY("식당 카테고리"),
    RESTAURANTNAME("식당 이름"),
    MEETINGNAME("모임 이름");

    private final String description;

    public static SearchType getSearchTypeIgnoringCase(String value) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new MeetingException(NOT_FOUND_SEARCH_TYPE));
    }
}
