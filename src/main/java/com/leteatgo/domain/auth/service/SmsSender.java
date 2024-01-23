package com.leteatgo.domain.auth.service;

import com.leteatgo.domain.auth.dto.request.SmsSendRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;

public interface SmsSender {

    SingleMessageSentResponse sendSms(SmsSendRequest request);

    String sendSmsTest(SmsSendRequest request);

}
