package com.leteatgo.domain.auth.service;

import com.leteatgo.domain.auth.dto.request.SmsSendRequest;

public interface SmsSender {

    void sendSms(SmsSendRequest request);

    String sendSmsTest(SmsSendRequest request);

}
