package com.leteatgo.domain.auth.controller;

import static com.leteatgo.global.util.CookieUtil.COOKIE_MAX_AGE;
import static com.leteatgo.global.util.CookieUtil.COOKIE_NAME;

import com.leteatgo.domain.auth.dto.request.EmailCheckRequest;
import com.leteatgo.domain.auth.dto.request.SignInRequest;
import com.leteatgo.domain.auth.dto.request.SignUpRequest;
import com.leteatgo.domain.auth.dto.request.SmsSendRequest;
import com.leteatgo.domain.auth.dto.request.SmsVerifyRequest;
import com.leteatgo.domain.auth.dto.response.SignUpResponse;
import com.leteatgo.domain.auth.service.AuthService;
import com.leteatgo.domain.auth.service.SmsSender;
import com.leteatgo.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;
    private final SmsSender smsSender;

    // 회원가입 (로컬)
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(
            @RequestBody @Valid SignUpRequest request
    ) {
        SignUpResponse response = authService.signUp(request);
        URI location = UriComponentsBuilder.fromUriString("/api/members/" + response.id())
                .build().toUri();
        return ResponseEntity.created(location).build();
    }

    // 로그인 (로컬)
    @PostMapping("/signin")
    public ResponseEntity<String> signIn(
            HttpServletResponse response,
            @RequestBody @Valid SignInRequest request
    ) {
        String token = authService.signIn(request);
        CookieUtil.addCookie(response, COOKIE_NAME, token, COOKIE_MAX_AGE);
        return ResponseEntity.ok().body(token);
    }

    // 로그아웃

    // 이메일 중복검사
    @PostMapping("/check-email")
    public ResponseEntity<Void> checkEmail(
            @RequestBody @Valid EmailCheckRequest request
    ) {
        authService.checkEmail(request);
        return ResponseEntity.ok().build();
    }

    // 핸드폰 인증번호 발송 (로컬 테스트용)
    @PostMapping("/send-sms")
    public ResponseEntity<String> sendSms(
            @RequestBody @Valid SmsSendRequest request
    ) {
        String authCode = smsSender.sendSmsTest(request);
        return ResponseEntity.ok().body(authCode);
    }

    // 핸드폰 인증번호 발송 (서비스 운영용)
//    @PostMapping("/send-sms")
//    public ResponseEntity<Void> sendSms(
//            @RequestBody @Valid SmsSendRequest request
//    ) {
//        smsService.sendSms(request);
//        return ResponseEntity.ok().build();
//    }

    // 핸드폰 인증번호 확인
    @PutMapping("/verify-sms")
    public ResponseEntity<Void> verifySms(
            @RequestBody @Valid SmsVerifyRequest request
    ) {
        authService.verifySmsAuthCode(request);
        return ResponseEntity.ok().build();
    }
}
