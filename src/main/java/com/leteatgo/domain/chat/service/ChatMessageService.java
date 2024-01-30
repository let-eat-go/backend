package com.leteatgo.domain.chat.service;

import com.leteatgo.domain.chat.dto.ChatMessageDto;
import com.leteatgo.domain.chat.entity.ChatMessage;
import com.leteatgo.domain.chat.entity.ChatRoom;
import com.leteatgo.domain.chat.repository.ChatMessageRepository;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.global.security.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final CustomUserDetailService userDetailService;

    @Async
    @Transactional
    public void saveMessage(ChatMessageDto message) {
        Member sender = userDetailService.findByIdOrThrow(message.senderId());
        ChatRoom chatRoom = chatRoomService.getChatRoomOrThrow(message.roomId());

        ChatMessage chatMessage = message.toEntity();
        chatMessage.setSender(sender);
        chatMessage.setChatRoom(chatRoom);

        chatMessageRepository.save(chatMessage);
    }
}
