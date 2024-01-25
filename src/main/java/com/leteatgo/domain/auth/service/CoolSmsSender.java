package com.leteatgo.domain.auth.service;

import static com.leteatgo.global.exception.ErrorCode.SMS_SEND_ERROR;

import com.leteatgo.domain.auth.dto.request.SmsSendRequest;
import com.leteatgo.domain.auth.entity.RedisSms;
import com.leteatgo.domain.auth.exception.AuthException;
import com.leteatgo.domain.auth.repository.RedisSmsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoolSmsSender implements SmsSender {

    private final RedisSmsRepository redisSmsRepository;
    private final DefaultMessageService defaultMessageService;

    @Value("${coolsms.from}")
    private String fromNumber;

    /* [문자 발송] 인증번호 생성 후 문자 발송 */
    @Override
    public void sendSms(SmsSendRequest request) {
        try {
            String authCode = generateAuthCode();
            redisSmsRepository.deleteById(request.phoneNumber());
            RedisSms redisSms = new RedisSms(request.phoneNumber(), authCode);
            redisSmsRepository.save(redisSms);

            String text = "[Web 발신]\n" + "[LetEatGo] 인증번호는 [" + authCode + "] 입니다.";

            Message message = new Message();
            message.setTo(request.phoneNumber());
            message.setFrom(fromNumber);
            message.setText(text);
            defaultMessageService.sendOne(new SingleMessageSendingRequest(message));
        } catch (Exception e) {
            log.error("SMS 전송 중 에러 발생", e);
            throw new AuthException(SMS_SEND_ERROR);
        }
    }

    /* [문자 발송] 로컬 테스트용 */
    @Override
    public String sendSmsTest(SmsSendRequest request) {
        try {
            String authCode = generateAuthCode();
            redisSmsRepository.deleteById(request.phoneNumber());
            RedisSms redisSms = new RedisSms(request.phoneNumber(), authCode);
            redisSmsRepository.save(redisSms);

            return authCode;
        } catch (Exception e) {
            log.error("SMS 전송 중 에러 발생", e);
            throw new AuthException(SMS_SEND_ERROR);
        }
    }

    private String generateAuthCode() {
        return RandomStringUtils.randomNumeric(6);
    }
}
