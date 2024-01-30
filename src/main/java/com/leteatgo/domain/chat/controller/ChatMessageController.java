package com.leteatgo.domain.chat.controller;

import com.leteatgo.domain.chat.dto.ChatMessageDto;
import com.leteatgo.domain.chat.service.ChatMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final static String DESTINATION = "/topic/chat/rooms/";

    @MessageMapping("/rooms/message")
    public void sendMessage(@Payload @Valid ChatMessageDto chatMessageDto) {
        chatMessageService.saveMessage(chatMessageDto);
        messagingTemplate.convertAndSend(DESTINATION + chatMessageDto.roomId(),
                chatMessageDto);
    }
}
