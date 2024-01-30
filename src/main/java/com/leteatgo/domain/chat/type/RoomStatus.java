package com.leteatgo.domain.chat.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomStatus {
    OPEN("개설"), CLOSE("종료");

    private final String description;
}
