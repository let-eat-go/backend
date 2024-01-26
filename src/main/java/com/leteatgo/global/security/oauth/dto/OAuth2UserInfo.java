package com.leteatgo.global.security.oauth.dto;

import static com.leteatgo.domain.member.type.MemberRole.ROLE_USER;

import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.type.LoginType;
import lombok.Builder;

@Builder
public record OAuth2UserInfo(
        String name,
        String email,
        String profileImage,
        LoginType loginType
) {

    public Member toEntity() {
        return Member.builder()
                .nickname(name)
                .email(email)
                .profileImage(profileImage)
                .loginType(loginType)
                .role(ROLE_USER)
                .build();
    }
}
