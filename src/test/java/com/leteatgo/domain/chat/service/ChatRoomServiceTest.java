package com.leteatgo.domain.chat.service;


import static com.leteatgo.domain.chat.type.RoomStatus.CLOSE;
import static com.leteatgo.domain.chat.type.RoomStatus.OPEN;
import static com.leteatgo.global.exception.ErrorCode.ACCESS_DENIED;
import static com.leteatgo.global.exception.ErrorCode.ALREADY_CLOSED_CHATROOM;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_CHATROOM;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEETING;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEMBER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.leteatgo.domain.chat.dto.response.ChatMessageResponse;
import com.leteatgo.domain.chat.dto.response.MyChatRoomResponse;
import com.leteatgo.domain.chat.entity.ChatMessage;
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
import com.leteatgo.domain.member.exception.MemberException;
import com.leteatgo.global.dto.CustomPageRequest;
import com.leteatgo.global.security.CustomUserDetailService;
import com.leteatgo.global.type.RestaurantCategory;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    MeetingRepository meetingRepository;

    @Mock
    ChatRoomRepository chatRoomRepository;

    @Mock
    ChatMessageRepository chatMessageRepository;

    @Mock
    CustomUserDetailService customUserDetailService;

    @Mock
    MeetingParticipantRepository meetingParticipantRepository;

    @InjectMocks
    ChatRoomService chatRoomService;

    @Nested
    @DisplayName("채팅방 개설 메서드는")
    class CreateChatRoomMethod {

        Long meetingId = 1L;
        Meeting meeting = Meeting.builder().build();
        ChatRoom chatRoom = new ChatRoom(OPEN, meeting);
        CreateChatRoomEvent event = new CreateChatRoomEvent(meetingId);

        @Test
        @DisplayName("생성된 모임이면 채팅방 개설에 성공한다.")
        void createChatRoom() {
            // given
            given(meetingRepository.findById(meetingId))
                    .willReturn(Optional.of(meeting));

            given(chatRoomRepository.save(any()))
                    .willReturn(chatRoom);

            // when
            // then
            assertThatCode(() -> chatRoomService.createChatRoom(event))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("존재하지 않는 모임이면 예외를 발생시킨다.")
        void createChatRoom_meeting_not_found() {
            // given
            given(meetingRepository.findById(meetingId))
                    .willThrow(new ChatException(NOT_FOUND_MEETING));

            // when
            // then
            assertThatThrownBy(() -> chatRoomService.createChatRoom(event))
                    .isInstanceOf(ChatException.class)
                    .hasMessageContaining(NOT_FOUND_MEETING.getErrorMessage());
        }
    }

    @Nested
    @DisplayName("채팅방 종료 메서드는")
    class CloseChatRoomMethod {
        Long meetingId = 1L;
        Meeting meeting = Meeting.builder().build();
        ChatRoom chatRoom = new ChatRoom(OPEN, meeting);
        CloseChatRoomEvent event = new CloseChatRoomEvent(meetingId);

        @Test
        @DisplayName("모임이 취소, 종료되면 채팅방이 종료됩니다.")
        void closeChatRoom() {
            // given
            given(meetingRepository.findById(meetingId))
                    .willReturn(Optional.of(meeting));

            given(chatRoomRepository.findByMeeting(meeting))
                    .willReturn(Optional.of(chatRoom));

            // when
            // then
            assertThatCode(() -> chatRoomService.closeChatRoom(event))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("존재하지 않는 모임이면 예외를 발생시킨다.")
        void createChatRoom_meeting_not_found() {
            // given
            given(meetingRepository.findById(meetingId))
                    .willThrow(new ChatException(NOT_FOUND_MEETING));

            // when
            // then
            assertThatThrownBy(() -> chatRoomService.closeChatRoom(event))
                    .isInstanceOf(ChatException.class)
                    .hasMessageContaining(NOT_FOUND_MEETING.getErrorMessage());
        }

        @Test
        @DisplayName("존재하지 않는 채팅방이면 예외를 발생시킨다.")
        void createChatRoom_chatRoom_not_found() {
            // given
            given(meetingRepository.findById(meetingId))
                    .willReturn(Optional.of(meeting));

            given(chatRoomRepository.findByMeeting(meeting))
                    .willThrow(new ChatException(NOT_FOUND_CHATROOM));

            // when
            // then
            assertThatThrownBy(() -> chatRoomService.closeChatRoom(event))
                    .isInstanceOf(ChatException.class)
                    .hasMessageContaining(NOT_FOUND_CHATROOM.getErrorMessage());
        }
    }

    @Nested
    @DisplayName("채팅방 대화 목록 조회 메서드는")
    class RoomMessagesMethod {
        String authId = "1";
        Long roomId = 1L;
        CustomPageRequest customPageRequest = new CustomPageRequest(1);

        Member member = Member.builder()
                .nickname("nick")
                .profileImage("profile")
                .build();

        Meeting meeting = Meeting.builder().build();

        ChatRoom chatRoom = new ChatRoom(OPEN, meeting);

        Pageable pageable = PageRequest.of(0, CustomPageRequest.PAGE_SIZE);
        ChatMessage chatMessage = new ChatMessage("message");
        List<ChatMessage> contents = List.of(chatMessage);

        @BeforeEach
        void setup() {
            ReflectionTestUtils.setField(member, "id", Long.parseLong(authId));
            chatMessage.setSender(member);
            meeting.addMeetingParticipant(member);
        }

        @Test
        @DisplayName("존재하는 채팅방이면 대화 목록을 조회할 수 있다.")
        void roomMessages() {
            // given
            given(chatRoomRepository.findChatRoomFetch(roomId))
                    .willReturn(Optional.of(chatRoom));

            given(chatMessageRepository.findByChatRoomFetch(chatRoom, pageable))
                    .willReturn(new SliceImpl<>(contents, pageable, true));

            // when
            Slice<ChatMessageResponse> chatMessageResponses =
                    chatRoomService.roomMessages(roomId, customPageRequest, authId);

            // then
            assertEquals(1, chatMessageResponses.getContent().size());
            assertTrue(chatMessageResponses.getContent().get(0).isRead());
        }

        @Test
        @DisplayName("존재하지 않는 채팅방이면 예외를 발생시킨다.")
        void roomMessages_not_found_chatRoom() {
            // given
            given(chatRoomRepository.findChatRoomFetch(roomId))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() ->
                    chatRoomService.roomMessages(roomId, customPageRequest, authId))
                    .isInstanceOf(ChatException.class)
                    .hasMessageContaining(NOT_FOUND_CHATROOM.getErrorMessage());
        }

        @Test
        @DisplayName("이미 종료된 채팅방이면 예외를 발생시킨다.")
        void roomMessages_already_closed() {
            // given
            given(chatRoomRepository.findChatRoomFetch(roomId))
                    .willReturn(Optional.of(new ChatRoom(CLOSE, meeting)));

            // when
            ChatException exception = assertThrows(ChatException.class, () ->
                    chatRoomService.roomMessages(roomId, customPageRequest, authId));

            // then
            assertEquals(ALREADY_CLOSED_CHATROOM, exception.getErrorCode());
        }

        @Test
        @DisplayName("참여하지 않은 모임의 채팅방이면 예외를 발생시킨다.")
        void roomMessages_access_deny() {
            // given
            given(chatRoomRepository.findChatRoomFetch(roomId))
                    .willReturn(Optional.of(new ChatRoom(CLOSE, Meeting.builder().build())));

            // when
            ChatException exception = assertThrows(ChatException.class, () ->
                    chatRoomService.roomMessages(roomId, customPageRequest, authId));

            // then
            assertEquals(ACCESS_DENIED, exception.getErrorCode());
        }

    }

    @Nested
    @DisplayName("내 채팅방 목록 조회 메서드는")
    class MyChatRoomMethod {
        String authId = "1";
        CustomPageRequest customPageRequest = new CustomPageRequest(1);
        Member member = Member.builder().build();
        Pageable pageable = PageRequest.of(customPageRequest.page(), 10);

        List<MyChatRoomResponse> contents = List.of(MyChatRoomResponse.builder()
                .meetingName("meeting name")
                .category(RestaurantCategory.ASIAN_CUISINE)
                .region("지역")
                .roomId(1L)
                .content("recent message")
                .isRead(false)
                .build());

        @Test
        @DisplayName("내가 참여한 열려있는 채팅방 목록을 조회할 수 있다.")
        void myChatRooms() {
            // given
            given(customUserDetailService.findByIdOrThrow(Long.parseLong(authId)))
                    .willReturn(member);

            given(meetingParticipantRepository.findAllMyChatRooms(member, pageable))
                    .willReturn(new SliceImpl<>(contents, pageable, false));

            // when
            Slice<MyChatRoomResponse> myChatRoomResponses =
                    chatRoomService.myChatRooms(authId, customPageRequest);

            // then
            assertEquals(1, myChatRoomResponses.getContent().size());
        }

        @Test
        @DisplayName("존재하지 않는 회원이면 예외를 발생시킨다.")
        void myChatRooms_not_found_member() {
            // given
            given(customUserDetailService.findByIdOrThrow(Long.parseLong(authId)))
                    .willThrow(new MemberException(NOT_FOUND_MEMBER));

            // when
            // then
            assertThatThrownBy(() ->
                    chatRoomService.myChatRooms(authId, customPageRequest))
                    .isInstanceOf(MemberException.class)
                    .hasMessageContaining(NOT_FOUND_MEMBER.getErrorMessage());
        }
    }
}