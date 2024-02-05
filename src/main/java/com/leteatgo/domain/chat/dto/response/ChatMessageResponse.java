package com.leteatgo.domain.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.leteatgo.domain.chat.entity.ChatMessage;
import com.leteatgo.domain.member.entity.Member;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ChatMessageResponse(
        Sender sender,
        String content,
        boolean isRead,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt
) {

    public static ChatMessageResponse fromEntity(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .sender(Sender.fromEntity(chatMessage.getSender()))
                .content(chatMessage.getContent())
                .isRead(chatMessage.isRead())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }

    @Builder
    public record Sender(
            Long senderId,
            String nickname,
            String profile
    ) {

        public static Sender fromEntity(Member member) {
            return Sender.builder()
                    .senderId(member.getId())
                    .nickname(member.getNickname())
                    .profile(member.getProfileImage())
                    .build();
        }
    }
}
