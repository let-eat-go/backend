package com.leteatgo.domain.notification.type;

public enum NotificationType {
    REMIND("모임 리마인드"),
    CANCEL("모임 취소"),
    COMPLETED("모임 완료");

    NotificationType(String description) {
    }

}
