package com.leteatgo.domain.meeting.exception;

import com.leteatgo.global.exception.CustomException;
import com.leteatgo.global.exception.ErrorCode;

public class MeetingException extends CustomException {
    public MeetingException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MeetingException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
