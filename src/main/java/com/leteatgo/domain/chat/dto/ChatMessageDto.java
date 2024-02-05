package com.leteatgo.domain.chat.dto;

import com.leteatgo.domain.chat.entity.ChatMessage;

public record ChatMessageDto(String message) {

    public ChatMessage toEntity() {
        return new ChatMessage(message);
    }
}
