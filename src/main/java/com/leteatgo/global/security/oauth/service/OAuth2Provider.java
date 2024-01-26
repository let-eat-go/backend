package com.leteatgo.global.security.oauth.service;

import static com.leteatgo.global.exception.ErrorCode.ILLEGAL_PROVIDER;

import com.leteatgo.domain.auth.exception.AuthException;
import com.leteatgo.domain.member.type.LoginType;
import com.leteatgo.global.security.oauth.dto.OAuth2UserInfo;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
@RequiredArgsConstructor
public enum OAuth2Provider {

    GOOGLE("google") {
        public OAuth2UserInfo toUserInfo(OAuth2User oauth2User) {
            Map<String, Object> attributes = oauth2User.getAttributes();

            return OAuth2UserInfo.builder()
                    .name((String) attributes.get("name"))
                    .email((String) attributes.get("email"))
                    .profileImage((String) attributes.get("picture"))
                    .loginType(LoginType.GOOGLE)
                    .build();
        }
    };

    private final String name;

    public static OAuth2Provider getOAuthProviderByName(String providerName) {
        return switch (providerName) {
            case "google" -> GOOGLE;
            // kakao, naver ...
            default -> throw new AuthException(ILLEGAL_PROVIDER);
        };
    }

    public abstract OAuth2UserInfo toUserInfo(OAuth2User oauth2User);
}
