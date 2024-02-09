package com.leteatgo.domain.notification.dto;

import com.leteatgo.domain.notification.entity.Notification;
import com.leteatgo.domain.notification.type.NotificationType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationDto(
        String message,
        NotificationType type,
        String relatedUrl,
        Boolean isRead,
        LocalDateTime createdAt
) {

    public static NotificationDto fromEntity(Notification notification) {
        return NotificationDto.builder()
                .message(notification.getContent())
                .type(notification.getType())
                .relatedUrl(notification.getRelatedUrl())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
