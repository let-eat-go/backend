package com.leteatgo.domain.member.dto.response;

import com.leteatgo.domain.member.entity.Member;
import lombok.Builder;

@Builder
public record MemberProfileResponse(
        Long memberId,
        String nickname,
        String profile,
        String introduce,
        Double mannerTemperature
) {

    public static MemberProfileResponse fromEntity(Member member) {
        return MemberProfileResponse.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profile(member.getProfileImage())
                .introduce(member.getIntroduce())
                .mannerTemperature(member.getMannerTemperature())
                .build();
    }
}
