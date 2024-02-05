package com.leteatgo.domain.chat.controller;

import static com.leteatgo.global.constants.Destination.CHAT_ROOM;

import com.leteatgo.domain.chat.dto.ChatMessageDto;
import com.leteatgo.domain.chat.service.ChatMessageService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/rooms/message")
    public void sendMessage(@Payload ChatMessageDto chatMessageDto, Principal principal) {
        chatMessageService.saveMessage(chatMessageDto, principal.getName());
        messagingTemplate.convertAndSend(CHAT_ROOM + chatMessageDto.roomId(),
                chatMessageDto);
    }
}
