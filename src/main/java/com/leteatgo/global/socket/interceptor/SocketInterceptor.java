package com.leteatgo.global.socket.interceptor;

import static com.leteatgo.global.exception.ErrorCode.EXPIRED_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.leteatgo.domain.auth.exception.TokenException;
import com.leteatgo.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
@Component
public class SocketInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider tokenProvider;

    // todo cookie를 받을 수 있으면 tokenFilter에서 연결 시 인증 처리?
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String accessToken = accessor.getFirstNativeHeader(AUTHORIZATION);

            if (tokenProvider.validateToken(accessToken)) {
                Authentication authentication = tokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                accessor.setUser(authentication);
            } else {
                throw new TokenException(EXPIRED_TOKEN);
            }
        }
        log.info("[WS] message header : {}", message.getHeaders());
        return message;
    }
}
