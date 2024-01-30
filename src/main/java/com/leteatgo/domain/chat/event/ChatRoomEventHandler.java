package com.leteatgo.domain.chat.event;

import com.leteatgo.domain.chat.event.dto.CloseChatRoomEvent;
import com.leteatgo.domain.chat.event.dto.CreateChatRoomEvent;
import com.leteatgo.domain.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChatRoomEventHandler {

    private final ChatRoomService chatRoomService;

    @Async
    @TransactionalEventListener
    public void handleCreateChatRoom(CreateChatRoomEvent event) {
        chatRoomService.createChatRoom(event);
    }

    @Async
    @TransactionalEventListener
    public void handleCloseChatRoom(CloseChatRoomEvent event) {
        chatRoomService.closeChatRoom(event);
    }
}
