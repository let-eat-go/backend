package com.leteatgo.domain.member.type;

import static com.leteatgo.global.exception.ErrorCode.INVALID_SEARCH_TYPE;

import com.leteatgo.domain.member.exception.MemberException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchType {

    SCHEDULED("모임 예정"),
    COMPLETED("모임 완료"),
    CREATED("내가 생성한 모임");

    private final String description;

    public static SearchType from(String type) {
        return Arrays.stream(values())
                .filter(o -> o.name().equals(type))
                .findFirst()
                .orElseThrow(() -> new MemberException(INVALID_SEARCH_TYPE));
    }
}
