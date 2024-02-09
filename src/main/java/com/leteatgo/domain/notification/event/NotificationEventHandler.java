package com.leteatgo.domain.notification.event;

import com.leteatgo.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener
    public void handleEvent(NotificationEvent event) {
        notificationService.sendNotification(event);
    }
}
