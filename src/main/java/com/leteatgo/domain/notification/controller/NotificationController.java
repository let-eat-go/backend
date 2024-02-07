package com.leteatgo.domain.notification.controller;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

import com.leteatgo.domain.notification.event.NotificationEvent;
import com.leteatgo.domain.notification.service.NotificationService;
import com.leteatgo.domain.notification.type.NotificationType;
import com.leteatgo.global.security.annotation.RoleUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = TEXT_EVENT_STREAM_VALUE)
    @RoleUser
    public ResponseEntity<SseEmitter> subscribe(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(notificationService.subscribe(userDetails.getUsername()));
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendNotification(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String message = "Test Message";
        NotificationEvent event = NotificationEvent.builder()
                .userId(userDetails.getUsername())
                .message(message)
                .type(NotificationType.REMIND)
                .relatedUrl("/test")
                .build();
        notificationService.sendNotification(event);
        return ResponseEntity.ok().build();
    }
}
