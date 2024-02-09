package com.leteatgo.domain.notification.event;

import com.leteatgo.domain.notification.type.NotificationType;
import lombok.Builder;

@Builder
public record NotificationEvent(
        String userId,
        String message,
        NotificationType type,
        String relatedUrl
) {

}
