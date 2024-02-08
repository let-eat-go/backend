package com.leteatgo.domain.chat.event;

import com.leteatgo.domain.chat.event.dto.CloseChatRoomEvent;
import com.leteatgo.domain.chat.event.dto.CreateChatRoomEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRoomEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishCreateChatRoom(CreateChatRoomEvent event) {
        eventPublisher.publishEvent(event);
    }

    public void publishCloseChatRoom(CloseChatRoomEvent event) {
        eventPublisher.publishEvent(event);
    }
}
