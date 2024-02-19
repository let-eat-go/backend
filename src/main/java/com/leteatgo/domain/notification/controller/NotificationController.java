package com.leteatgo.domain.notification.controller;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

import com.leteatgo.domain.notification.dto.NotificationDto;
import com.leteatgo.domain.notification.event.NotificationEvent;
import com.leteatgo.domain.notification.service.NotificationService;
import com.leteatgo.domain.notification.type.NotificationType;
import com.leteatgo.global.dto.CustomPageRequest;
import com.leteatgo.global.dto.SliceResponse;
import com.leteatgo.global.security.annotation.RoleUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 알림 구독
    @GetMapping(value = "/subscribe", produces = TEXT_EVENT_STREAM_VALUE)
    @RoleUser
    public ResponseEntity<SseEmitter> subscribe(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(notificationService.subscribe(userDetails.getUsername()));
    }

    // 알림 읽음 처리
    @PatchMapping("/{notificationId}")
    @RoleUser
    public ResponseEntity<Void> readNotification(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long notificationId
    ) {
        notificationService.readNotification(
                Long.parseLong(userDetails.getUsername()), notificationId);
        return ResponseEntity.ok().build();
    }

    // 알림 목록 조회
    @GetMapping("/list")
    @RoleUser
    public ResponseEntity<SliceResponse<NotificationDto>> getNotificationList(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid CustomPageRequest request
    ) {
        Slice<NotificationDto> response = notificationService.getNotificationList(
                userDetails.getUsername(), request);
        return ResponseEntity.ok(new SliceResponse<>(response));
    }

    // 알림 보내기 (테스트용)
    @PostMapping("/send-test/{type}")
    @RoleUser
    public ResponseEntity<Void> sendTestNotification(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable NotificationType type
    ) {
        NotificationEvent event = NotificationEvent.builder()
                .userId(userDetails.getUsername())
                .message("테스트 알림입니다.")
                .type(type)
                .relatedUrl("/test")
                .build();
        notificationService.sendNotification(event);
        return ResponseEntity.ok().build();
    }
}
