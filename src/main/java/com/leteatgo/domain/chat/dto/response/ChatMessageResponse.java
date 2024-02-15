package com.leteatgo.domain.chat.dto.response;

import com.leteatgo.domain.chat.entity.ChatMessage;
import com.leteatgo.domain.member.entity.Member;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ChatMessageResponse(
        Long chatId,
        Sender sender,
        String content,
        boolean isRead,
        LocalDateTime createdAt
) {

    public static ChatMessageResponse fromEntity(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .chatId(chatMessage.getId())
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
