package com.leteatgo.domain.member.service;

import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEMBER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import com.leteatgo.domain.member.dto.response.MyInfoResponse;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.exception.MemberException;
import com.leteatgo.domain.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    MemberService memberService;

    @Nested
    @DisplayName("내 정보 조회 메서드")
    class MyInformationMethod {

        String authId = "1";
        Long memberId = Long.parseLong(authId);
        Member member = Member.builder()
                .nickname("nick")
                .profileImage("profile url")
                .introduce("introduce")
                .build();

        @Test
        @DisplayName("성공")
        void myInformation() {
            // given
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.of(member));

            // when
            MyInfoResponse myInfoResponse = memberService.myInformation(authId);

            // then
            assertEquals("nick", myInfoResponse.nickname());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 회원")
        void myInformation_not_found_member() {
            // given
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() ->
                    memberService.myInformation(authId))
                    .isInstanceOf(MemberException.class)
                    .hasMessageContaining(NOT_FOUND_MEMBER.getErrorMessage());
        }
    }
}