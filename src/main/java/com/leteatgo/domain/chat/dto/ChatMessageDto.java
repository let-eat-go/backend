package com.leteatgo.domain.chat.dto;

import com.leteatgo.domain.chat.entity.ChatMessage;
import jakarta.validation.constraints.NotNull;

public record ChatMessageDto(
        @NotNull
        Long roomId,

        @NotNull
        Long senderId,

        @NotNull
        String message
) {

    public ChatMessage toEntity() {
        return new ChatMessage(message);
    }
}
