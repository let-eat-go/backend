package com.leteatgo.domain.member.service;

import static com.leteatgo.global.exception.ErrorCode.ALREADY_DELETED_MEMBER;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEMBER;
import static com.leteatgo.global.type.RestaurantCategory.ASIAN_CUISINE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import com.leteatgo.domain.meeting.repository.MeetingParticipantRepository;
import com.leteatgo.domain.member.dto.request.UpdateInfoRequest;
import com.leteatgo.domain.member.dto.response.MemberProfileResponse;
import com.leteatgo.domain.member.dto.response.MyMeetingsResponse;
import com.leteatgo.domain.member.dto.response.MyMeetingsResponse.Restaurant;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.exception.MemberException;
import com.leteatgo.domain.member.repository.MemberRepository;
import com.leteatgo.domain.member.type.SearchType;
import com.leteatgo.global.dto.CustomPageRequest;
import com.leteatgo.global.storage.StorageService;
import com.leteatgo.global.storage.s3.S3FileDto;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ObjectUtils;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    StorageService storageService;

    @Mock
    MeetingParticipantRepository meetingParticipantRepository;

    @InjectMocks
    MemberService memberService;

    Long memberId = 1L;
    Member member = Member.builder()
            .nickname("nick")
            .profileImage("profile url")
            .introduce("introduce")
            .build();

    @Nested
    @DisplayName("내 정보 조회 메서드")
    class MyInformationMethod {

        @Test
        @DisplayName("성공")
        void myInformation() {
            // given
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.of(member));

            // when
            MemberProfileResponse memberProfileResponse = memberService.getProfile(memberId);

            // then
            assertEquals("nick", memberProfileResponse.nickname());
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
                    memberService.getProfile(memberId))
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
                    memberService.getProfile(memberId))
                    .isInstanceOf(MemberException.class)
                    .hasMessageContaining(ALREADY_DELETED_MEMBER.getErrorMessage());
        }
    }

    @Nested
    @DisplayName("회원 수정 메서드")
    class UpdateInfo {

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

    @Nested
    @DisplayName("내 모임 목록 조회 메서드")
    class MyMeetingsMethod {

        SearchType type = SearchType.CREATED;
        CustomPageRequest request = new CustomPageRequest(1);

        MyMeetingsResponse response = MyMeetingsResponse.builder()
                .meetingId(1L)
                .meetingName("모여라 참깨")
                .category(ASIAN_CUISINE)
                .maxParticipants(3)
                .restaurant(Restaurant.builder()
                        .id(1L)
                        .name("어머니대성집")
                        .address("서울 동대문구 왕산로11길 4")
                        .phoneNumber("02-123-1234")
                        .build())
                .build();

        List<MyMeetingsResponse> contents = List.of(response);
        Pageable pageable = PageRequest.of(request.page(), CustomPageRequest.PAGE_SIZE);
        SliceImpl<MyMeetingsResponse> slice = new SliceImpl<>(contents, pageable, true);

        @Test
        @DisplayName("성공")
        void myMeetings() {
            // given
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.of(member));

            given(meetingParticipantRepository.findAllMyMeetings(member, type, pageable))
                    .willReturn(slice);

            // when
            Slice<MyMeetingsResponse> responses =
                    memberService.myMeetings(type, request, memberId);

            // then
            assertEquals(1, responses.getContent().size());
            assertEquals(0, responses.getPageable().getPageNumber());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 회원")
        void myMeetings_not_found_member() {
            // given
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() ->
                    memberService.myMeetings(type, request, memberId))
                    .isInstanceOf(MemberException.class)
                    .hasMessageContaining(NOT_FOUND_MEMBER.getErrorMessage());
        }
    }
}