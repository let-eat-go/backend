package com.leteatgo.domain.notification.dto.request;

import com.leteatgo.domain.notification.type.NotificationType;

public record TestNotificationRequest(
        String message,
        NotificationType type,
        String relatedUrl
) {

}
