package com.leteatgo.domain.notification.dto;

import com.leteatgo.domain.notification.entity.Notification;
import com.leteatgo.domain.notification.type.NotificationType;
import lombok.Builder;

@Builder
public record NotificationDto(
        String message,
        NotificationType type,
        String relatedUrl,
        Boolean isRead
) {

    public static NotificationDto fromEntity(Notification notification) {
        return NotificationDto.builder()
                .message(notification.getContent())
                .type(notification.getType())
                .relatedUrl(notification.getRelatedUrl())
                .isRead(notification.getIsRead())
                .build();
    }
}
