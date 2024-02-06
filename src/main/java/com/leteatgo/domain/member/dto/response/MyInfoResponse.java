package com.leteatgo.domain.member.dto.response;

import com.leteatgo.domain.member.entity.Member;
import lombok.Builder;

@Builder
public record MyInfoResponse(
        String nickname,
        String profile,
        String introduce,
        Double mannerTemperature
) {

    public static MyInfoResponse fromEntity(Member member) {
        return MyInfoResponse.builder()
                .nickname(member.getNickname())
                .profile(member.getProfileImage())
                .introduce(member.getIntroduce())
                .mannerTemperature(member.getMannerTemperature())
                .build();
    }
}
