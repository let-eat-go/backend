package com.leteatgo.domain.meeting.type;

public enum MeetingStatus {
    BEFORE("모임 시작 전"),
    IN_PROGRESS("모임 진행 중"),
    COMPLETED("모임 완료"),
    CANCELED("모임 취소");

    MeetingStatus(String description) {
    }
}
