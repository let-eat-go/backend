package com.leteatgo.domain.chat.dto;

import jakarta.validation.constraints.NotNull;

public record ChatMessageDto(
        @NotNull
        Long roomId,

        @NotNull
        Long senderId,

        @NotNull
        String message
) {

    public com.leteatgo.domain.chat.entity.ChatMessage toEntity() {
        return new com.leteatgo.domain.chat.entity.ChatMessage(message);
    }
}
