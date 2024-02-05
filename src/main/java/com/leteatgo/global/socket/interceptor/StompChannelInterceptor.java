package com.leteatgo.global.socket.interceptor;

import static com.leteatgo.global.constants.Destination.CHAT_ROOM;
import static com.leteatgo.global.exception.ErrorCode.ACCESS_DENIED;
import static com.leteatgo.global.exception.ErrorCode.ALREADY_CLOSED_CHATROOM;
import static com.leteatgo.global.exception.ErrorCode.EXPIRED_TOKEN;
import static com.leteatgo.global.exception.ErrorCode.ILLEGAL_DESTINATION;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_CHATROOM;
import static com.leteatgo.global.exception.ErrorCode.NOT_JOINED_CHATROOM;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.leteatgo.domain.auth.entity.RedisToken;
import com.leteatgo.domain.auth.exception.TokenException;
import com.leteatgo.domain.auth.service.TokenService;
import com.leteatgo.domain.chat.entity.ChatRoom;
import com.leteatgo.domain.chat.exception.ChatException;
import com.leteatgo.domain.chat.repository.ChatRoomRepository;
import com.leteatgo.domain.chat.type.RoomStatus;
import com.leteatgo.global.security.jwt.JwtTokenProvider;
import java.security.Principal;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider tokenProvider;
    private final TokenService tokenService;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * todo
     * 클라이언트에서 websocket 연결 시 http 프로토콜에 cookie를 보낼 수 있으면 tokenFilter에서 인증 처리 (WebsocketSecurity 사용 가능, 이게 best일 듯하다.)
     * header로 보낸다면 handshakeInterceptor에서 토큰 파싱 후 여기로 전달 (WebsocketSecurity 사용 불가 -> filter에서 처리한다면 사용 가능)
     * 아래는 Stomp 프로토콜 헤더에 토큰을 담아 전달했을 시 인증 처리
     **/
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        assert accessor != null;
        if (StompCommand.CONNECT.equals(accessor.getCommand())) { // 연결 인증 처리
            String accessToken = accessor.getFirstNativeHeader(AUTHORIZATION);

            // 인증 시 refreshToken이 있는 경우 인증 성공으로 처리 (재발급은 하지 않음)
            if (tokenProvider.validateToken(accessToken)) {
                setAuthentication(accessToken, accessor);
            } else {
                RedisToken token = tokenService.getTokenByAccessToken(accessToken);
                String refreshToken = token.getRefreshToken();

                if (!tokenProvider.validateToken(refreshToken)) {
                    throw new TokenException(EXPIRED_TOKEN);
                }

                setAuthentication(accessToken, accessor);
            }
            log.info("[WS] connection successful");
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) { // 채팅방 구독 권한 확인
            Long memberId = getMemberId(accessor.getUser());
            Long roomId = parseRoomId(accessor.getDestination());

            validateChatRoom(roomId);

            ChatRoom chatRoom = chatRoomRepository.findChatRoomFetch(roomId)
                    .orElseThrow(() -> new ChatException(NOT_JOINED_CHATROOM));

            checkAccessChatRoom(chatRoom, memberId);

            // 현재 구독한 채팅방으로만 메세지 전송
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            sessionAttributes.put("roomId", roomId);

            log.info("[WS] subscribed chat room [{}]", roomId);
        }

        return message;
    }

    private void setAuthentication(String accessToken, StompHeaderAccessor accessor) {
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        accessor.setUser(authentication);
    }

    private void validateChatRoom(Long roomId) {
        if (!chatRoomRepository.existsById(roomId)) {
            throw new ChatException(NOT_FOUND_CHATROOM);
        }
    }

    private void checkAccessChatRoom(ChatRoom chatRoom, Long memberId) {
        boolean match = chatRoom.getMeeting().getMeetingParticipants()
                .stream().anyMatch(participant ->
                        Objects.equals(participant.getMember().getId(), memberId));

        if (!match) {
            throw new ChatException(ACCESS_DENIED);
        }

        if (chatRoom.getStatus() == RoomStatus.CLOSE) {
            throw new ChatException(ALREADY_CLOSED_CHATROOM);
        }
    }

    private Long getMemberId(Principal user) {
        if (ObjectUtils.isEmpty(user)) {
            throw new ChatException(ACCESS_DENIED);
        }
        return Long.parseLong(user.getName());
    }

    private Long parseRoomId(String destination) {
        if (ObjectUtils.isEmpty(destination) || !destination.startsWith(CHAT_ROOM) ||
                destination.length() == CHAT_ROOM.length()) {
            throw new ChatException(ILLEGAL_DESTINATION);
        }
        return Long.parseLong(destination.substring(CHAT_ROOM.length()));
    }
}
