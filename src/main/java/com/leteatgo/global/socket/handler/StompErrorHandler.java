package com.leteatgo.global.socket.handler;

import com.leteatgo.domain.auth.exception.TokenException;
import com.leteatgo.domain.chat.exception.ChatException;
import com.leteatgo.global.exception.ErrorCode;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Component
public class StompErrorHandler extends StompSubProtocolErrorHandler {

    private static final byte[] EMPTY_PAYLOAD = new byte[0];

    public StompErrorHandler() {
        super();
    }

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage,
            Throwable ex) {
        if (ex instanceof MessageDeliveryException) {
            ex = ex.getCause();
        }

        if (ex instanceof TokenException) {
            return handleTokenException(((TokenException) ex).getErrorCode());
        }

        if (ex instanceof ChatException) {
            return handleChatException(((ChatException) ex).getErrorCode());
        }

        return handleException(ex);
    }

    private Message<byte[]> handleException(Throwable ex) {
        return errorMessage(ex.getMessage(), HttpStatus.BAD_REQUEST.name());
    }

    private Message<byte[]> handleChatException(ErrorCode errorCode) {
        return errorMessage(errorCode.getErrorMessage(), errorCode.name());
    }

    private Message<byte[]> handleTokenException(ErrorCode errorCode) {
        return errorMessage(errorCode.getErrorMessage(), errorCode.name());
    }

    private Message<byte[]> errorMessage(String errorMessage, String errorCode) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(errorCode);
        accessor.setLeaveMutable(true);

        String response = String.format("[%s] %s", errorCode, errorMessage);
        return MessageBuilder.createMessage(errorMessage != null ?
                        response.getBytes(StandardCharsets.UTF_8) : EMPTY_PAYLOAD,
                accessor.getMessageHeaders());
    }
}
