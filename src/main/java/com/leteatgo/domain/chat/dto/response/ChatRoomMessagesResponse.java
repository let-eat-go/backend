package com.leteatgo.domain.chat.dto.response;

import com.leteatgo.domain.chat.entity.ChatMessage;
import com.leteatgo.domain.chat.entity.ChatRoom;
import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.member.entity.Member;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ChatRoomMessagesResponse(
        MeetingInfo meetingInfo,
        Sender sender,
        Long chatId,
        String content,
        boolean isRead,
        LocalDateTime createdAt
) {

    public static ChatRoomMessagesResponse fromEntity(ChatMessage chatMessage, ChatRoom chatRoom) {
        return ChatRoomMessagesResponse.builder()
                .meetingInfo(MeetingInfo.fromEntity(chatRoom.getMeeting()))
                .sender(ChatRoomMessagesResponse.Sender.fromEntity(chatMessage.getSender()))
                .chatId(chatMessage.getId())
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
            return ChatRoomMessagesResponse.Sender.builder()
                    .senderId(member.getId())
                    .nickname(member.getNickname())
                    .profile(member.getProfileImage())
                    .build();
        }
    }

    @Builder
    public record MeetingInfo(
            String meetingName,
            String region,
            LocalDateTime startDateTime
    ) {

        public static MeetingInfo fromEntity(Meeting meeting) {
            return MeetingInfo.builder()
                    .meetingName(meeting.getName())
                    .region(meeting.getRegion().getName())
                    .startDateTime(meeting.getStartDateTime())
                    .build();
        }

    }
}
