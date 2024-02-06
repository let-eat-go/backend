package com.leteatgo.domain.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.leteatgo.global.type.RestaurantCategory;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record MyChatRoomResponse(
        String meetingName,
        RestaurantCategory category,
        String region,
        Chat chat
) {

    @Builder
    public record Chat(
            Long roomId,
            String content,
            boolean isRead,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime createdAt
    ) {

    }
}
