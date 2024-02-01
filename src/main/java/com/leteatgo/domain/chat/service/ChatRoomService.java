package com.leteatgo.domain.chat.service;

import static com.leteatgo.domain.chat.type.RoomStatus.OPEN;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_CHATROOM;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEETING;

import com.leteatgo.domain.chat.dto.response.ChatMessageResponse;
import com.leteatgo.domain.chat.dto.response.MyChatRoomResponse;
import com.leteatgo.domain.chat.entity.ChatRoom;
import com.leteatgo.domain.chat.event.dto.CloseChatRoomEvent;
import com.leteatgo.domain.chat.event.dto.CreateChatRoomEvent;
import com.leteatgo.domain.chat.exception.ChatException;
import com.leteatgo.domain.chat.repository.ChatMessageRepository;
import com.leteatgo.domain.chat.repository.ChatRoomRepository;
import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.repository.MeetingParticipantRepository;
import com.leteatgo.domain.meeting.repository.MeetingRepository;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.global.dto.CustomPageRequest;
import com.leteatgo.global.security.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final MeetingRepository meetingRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final CustomUserDetailService userDetailService;
    private final MeetingParticipantRepository meetingParticipantRepository;

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

    @Transactional
    public Slice<ChatMessageResponse> roomMessages(Long roomId, CustomPageRequest request) {
        ChatRoom chatRoom = getChatRoomOrThrow(roomId);

        return chatMessageRepository.findByChatRoomFetch(chatRoom,
                        PageRequest.of(request.page(), CustomPageRequest.PAGE_SIZE))
                .map(chatMessage -> {
                    if (!chatMessage.isRead()) { // 읽음 처리
                        chatMessage.setRead();
                    }
                    return ChatMessageResponse.fromEntity(chatMessage);
                });
    }

    public Slice<MyChatRoomResponse> myChatRooms(String authId, CustomPageRequest request) {
        Member member = userDetailService.findByIdOrThrow(Long.parseLong(authId));
        return meetingParticipantRepository.findByMemberFetch(
                member, PageRequest.of(request.page(), CustomPageRequest.PAGE_SIZE));
    }

    private Meeting getMeetingOrThrow(Long event) {
        return meetingRepository.findById(event)
                .orElseThrow(() -> new ChatException(NOT_FOUND_MEETING));
    }
}
