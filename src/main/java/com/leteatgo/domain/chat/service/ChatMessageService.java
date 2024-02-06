package com.leteatgo.domain.chat.service;

import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_CHATROOM;

import com.leteatgo.domain.chat.dto.ChatMessageDto;
import com.leteatgo.domain.chat.dto.response.ChatMessageResponse;
import com.leteatgo.domain.chat.entity.ChatMessage;
import com.leteatgo.domain.chat.entity.ChatRoom;
import com.leteatgo.domain.chat.exception.ChatException;
import com.leteatgo.domain.chat.repository.ChatMessageRepository;
import com.leteatgo.domain.chat.repository.ChatRoomRepository;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.global.security.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final CustomUserDetailService userDetailService;

    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageDto message, String authId, Long roomId) {
        Member sender = userDetailService.findByIdOrThrow(Long.parseLong(authId));

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(NOT_FOUND_CHATROOM));

        ChatMessage chatMessage = message.toEntity();
        chatMessage.setSender(sender);
        chatMessage.setChatRoom(chatRoom);

        return ChatMessageResponse.fromEntity(chatMessageRepository.save(chatMessage));
    }
}
