package com.leteatgo.domain.auth.service;

import static com.leteatgo.domain.member.type.LoginType.LOCAL;
import static com.leteatgo.global.exception.ErrorCode.ALREADY_EXIST_EMAIL;
import static com.leteatgo.global.exception.ErrorCode.ALREADY_EXIST_NICKNAME;
import static com.leteatgo.global.exception.ErrorCode.ALREADY_EXIST_PHONE_NUMBER;
import static com.leteatgo.global.exception.ErrorCode.PHONE_NUMBER_NOT_VERIFIED;
import static com.leteatgo.global.exception.ErrorCode.WRONG_PASSWORD;
import static com.leteatgo.global.exception.ErrorCode.WRONG_PHONE_NUMBER;

import com.leteatgo.domain.auth.dto.request.EmailCheckRequest;
import com.leteatgo.domain.auth.dto.request.SignInRequest;
import com.leteatgo.domain.auth.dto.request.SignUpRequest;
import com.leteatgo.domain.auth.dto.request.SmsVerifyRequest;
import com.leteatgo.domain.auth.dto.response.SignUpResponse;
import com.leteatgo.domain.auth.entity.RedisSms;
import com.leteatgo.domain.auth.entity.RedisToken;
import com.leteatgo.domain.auth.exception.AuthException;
import com.leteatgo.domain.auth.repository.RedisSmsRepository;
import com.leteatgo.domain.auth.repository.RedisTokenRepository;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.repository.MemberRepository;
import com.leteatgo.global.security.CustomUserDetailService;
import com.leteatgo.global.security.CustomUserDetails;
import com.leteatgo.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final RedisSmsRepository redisSmsRepository;
    private final RedisTokenRepository redisTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailService customUserDetailService;


    /* [회원가입] 닉네임 중복검사, 비밀번호 일치 검사, 핸드폰 인증 검사 후 회원가입 */
    public SignUpResponse signUp(SignUpRequest request) {
        validateDuplicateNickName(request.nickname());
        validatePasswordMatch(request.password(), request.passwordCheck());
        validatePhoneNumberVerified(request.phoneNumber());

        String password = passwordEncoder.encode(request.password());
        Member member = SignUpRequest.toEntity(request, password, LOCAL);
        memberRepository.save(member);

        return new SignUpResponse(member.getId());
    }

    private void validateDuplicateNickName(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new AuthException(ALREADY_EXIST_NICKNAME);
        }
    }

    private void validatePasswordMatch(String password, String passwordCheck) {
        if (!password.equals(passwordCheck)) {
            throw new AuthException(WRONG_PASSWORD);
        }
    }

    private void validatePhoneNumberVerified(String phoneNumber) {
        if (memberRepository.existsByPhoneNumber(phoneNumber)) {
            throw new AuthException(ALREADY_EXIST_PHONE_NUMBER);
        }
        RedisSms redisSms = redisSmsRepository.findById(phoneNumber)
                .orElseThrow(() -> new AuthException(WRONG_PHONE_NUMBER));
        if (!redisSms.isVerified()) {
            throw new AuthException(PHONE_NUMBER_NOT_VERIFIED);
        }
    }

    /* [이메일 중복검사] 이미 존재하는 이메일이면 예외 발생 */
    public void checkEmail(EmailCheckRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new AuthException(ALREADY_EXIST_EMAIL);
        }
    }

    /* [핸드폰 인증번호 검증] 인증번호가 일치하지 않으면 예외 발생 */
    public void verifySmsAuthCode(SmsVerifyRequest request) {
        RedisSms redisSms = redisSmsRepository.findById(request.phoneNumber())
                .orElseThrow(() -> new AuthException(WRONG_PHONE_NUMBER));
        redisSms.validateAuthCode(request.authCode());
        redisSms.verify();
        redisSmsRepository.save(redisSms);
    }

    /* [로그인] 성공하면 토큰 발급 */
    public String signIn(SignInRequest request) {
        CustomUserDetails userDetails = getUserDetails(request.email());
        checkPassword(request.password(), userDetails.getPassword());
        Authentication authentication = createAuthentication(userDetails);

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        RedisToken token = new RedisToken(userDetails.getId(), refreshToken, accessToken);
        redisTokenRepository.save(token);

        return accessToken;
    }

    private CustomUserDetails getUserDetails(String email) {
        return customUserDetailService.loadUserByUsername(email);
    }

    private Authentication createAuthentication(CustomUserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
    }

    private void checkPassword(String password, String encodedPassword) {
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new AuthException(WRONG_PASSWORD);
        }
    }

}
