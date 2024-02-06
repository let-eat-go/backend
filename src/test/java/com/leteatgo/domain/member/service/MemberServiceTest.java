package com.leteatgo.domain.member.service;

import static com.leteatgo.global.exception.ErrorCode.ALREADY_DELETED_MEMBER;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEMBER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import com.leteatgo.domain.member.dto.request.UpdateInfoRequest;
import com.leteatgo.domain.member.dto.response.MyInfoResponse;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.exception.MemberException;
import com.leteatgo.domain.member.repository.MemberRepository;
import com.leteatgo.global.storage.StorageService;
import com.leteatgo.global.storage.s3.S3FileDto;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ObjectUtils;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    StorageService storageService;

    @InjectMocks
    MemberService memberService;

    @Nested
    @DisplayName("내 정보 조회 메서드")
    class MyInformationMethod {

        Long memberId = Long.parseLong("1");
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
            MyInfoResponse myInfoResponse = memberService.myInformation(memberId);

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
                    memberService.myInformation(memberId))
                    .isInstanceOf(MemberException.class)
                    .hasMessageContaining(NOT_FOUND_MEMBER.getErrorMessage());
        }

        @Test
        @DisplayName("실패 - 이미 삭제된 회원")
        void myInformation_already_deleted() {
            // given
            member.setDeletedAt(LocalDateTime.now());

            given(memberRepository.findById(memberId))
                    .willReturn(Optional.of(member));

            // when
            // then
            assertThatThrownBy(() ->
                    memberService.myInformation(memberId))
                    .isInstanceOf(MemberException.class)
                    .hasMessageContaining(ALREADY_DELETED_MEMBER.getErrorMessage());
        }
    }

    @Nested
    @DisplayName("회원 수정 메서드")
    class UpdateInfo {

        Long memberId = 1L;
        Member member = Member.builder()
                .nickname("nick")
                .profileImage("profile url")
                .introduce("introduce")
                .build();
        UpdateInfoRequest request = new UpdateInfoRequest("new nick",
                "new introduce");
        MockMultipartFile profile;
        S3FileDto fileDto = new S3FileDto("url", "filename");

        @BeforeEach
        void setup() throws IOException {
            profile = new MockMultipartFile(
                    "profile",
                    "profile.jpeg",
                    "image/jpeg",
                    new FileInputStream("src/test/resources/img/profile.jpeg")
            );
        }

        @Test
        @DisplayName("성공")
        void updateInfo() {
            // given
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.of(member));

            given(storageService.uploadFile(profile))
                    .willReturn(fileDto);

            // when
            memberService.updateInfo(request, profile, memberId);

            // then
            assertEquals("new nick", member.getNickname());
            assertEquals("new introduce", member.getIntroduce());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 회원")
        void updateInfo_not_found_member() {
            // given
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() ->
                    memberService.updateInfo(request, profile, memberId))
                    .isInstanceOf(MemberException.class)
                    .hasMessageContaining(NOT_FOUND_MEMBER.getErrorMessage());
        }

        @Test
        @DisplayName("실패 - 이미 삭제된 회원")
        void updateInfo_already_deleted() {
            // given
            member.setDeletedAt(LocalDateTime.now());

            given(memberRepository.findById(memberId))
                    .willReturn(Optional.of(member));

            // when
            // then
            assertThatThrownBy(() ->
                    memberService.updateInfo(request, profile, memberId))
                    .isInstanceOf(MemberException.class)
                    .hasMessageContaining(ALREADY_DELETED_MEMBER.getErrorMessage());
        }
    }

    @Nested
    @DisplayName("회원 삭제 메서드")
    class DeleteMemberMethod {

        Long memberId = 1L;
        Member member = Member.builder()
                .nickname("nick")
                .profileImage("profile url")
                .introduce("introduce")
                .build();

        @Test
        @DisplayName("성공")
        void deleteMember() {
            // given
            member.addProfile("url", "filename");

            given(memberRepository.findById(memberId))
                    .willReturn(Optional.of(member));

            doNothing().when(storageService).deleteFile(member.getProfileFilename());

            // when
            memberService.deleteMember(memberId);

            // then
            assertFalse(ObjectUtils.isEmpty(member.getDeletedAt()));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 회원")
        void deleteMember_not_found_member() {
            // given
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() ->
                    memberService.deleteMember(memberId))
                    .isInstanceOf(MemberException.class)
                    .hasMessageContaining(NOT_FOUND_MEMBER.getErrorMessage());
        }

        @Test
        @DisplayName("실패 - 이미 삭제된 회원")
        void deleteMember_already_deleted() {
            // given
            member.setDeletedAt(LocalDateTime.now());

            given(memberRepository.findById(memberId))
                    .willReturn(Optional.of(member));

            // when
            // then
            assertThatThrownBy(() ->
                    memberService.deleteMember(memberId))
                    .isInstanceOf(MemberException.class)
                    .hasMessageContaining(ALREADY_DELETED_MEMBER.getErrorMessage());
        }
    }
}