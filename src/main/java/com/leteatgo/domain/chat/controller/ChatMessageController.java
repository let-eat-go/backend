package com.leteatgo.domain.chat.controller;

import static com.leteatgo.global.constants.Destination.CHAT_ROOM;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.SESSION_ATTRIBUTES;

import com.leteatgo.domain.chat.dto.ChatMessageDto;
import com.leteatgo.domain.chat.dto.response.ChatMessageResponse;
import com.leteatgo.domain.chat.service.ChatMessageService;
import java.security.Principal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/rooms/message")
    public void sendMessage(@Payload ChatMessageDto chatMessageDto, Principal principal,
            @Header(SESSION_ATTRIBUTES) Map<String, Object> sessionAttributes) {
        Long roomId = (Long) sessionAttributes.get("roomId");
        ChatMessageResponse chatMessageResponse = chatMessageService.saveMessage(chatMessageDto,
                principal.getName(), roomId);

        messagingTemplate.convertAndSend(CHAT_ROOM + roomId, chatMessageResponse);
        log.info("[ws] send message [{}]", roomId);
    }

}
