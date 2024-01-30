package com.leteatgo.domain.chat.service;

import static com.leteatgo.domain.chat.type.RoomStatus.OPEN;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_CHATROOM;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEETING;

import com.leteatgo.domain.chat.event.dto.CloseChatRoomEvent;
import com.leteatgo.domain.chat.event.dto.CreateChatRoomEvent;
import com.leteatgo.domain.chat.entity.ChatRoom;
import com.leteatgo.domain.chat.exception.ChatException;
import com.leteatgo.domain.chat.repository.ChatRoomRepository;
import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final MeetingRepository meetingRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom getChatRoomOrThrow(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(NOT_FOUND_CHATROOM));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createChatRoom(CreateChatRoomEvent event) {
        Meeting meeting = getMeetingOrThrow(event.meetingId());

        chatRoomRepository.save(new ChatRoom(OPEN, meeting));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void closeChatRoom(CloseChatRoomEvent event) {
        Meeting meeting = getMeetingOrThrow(event.meetingId());

        ChatRoom chatRoom = chatRoomRepository.findByMeeting(meeting)
                .orElseThrow(() -> new ChatException(NOT_FOUND_CHATROOM));

        chatRoom.closeChatRoom();
    }

    private Meeting getMeetingOrThrow(Long event) {
        return meetingRepository.findById(event)
                .orElseThrow(() -> new ChatException(NOT_FOUND_MEETING));
    }
}
