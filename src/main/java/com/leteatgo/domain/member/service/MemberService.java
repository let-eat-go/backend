package com.leteatgo.domain.member.service;

import static com.leteatgo.global.exception.ErrorCode.ALREADY_DELETED_MEMBER;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEMBER;

import com.leteatgo.domain.meeting.repository.MeetingParticipantRepository;
import com.leteatgo.domain.member.dto.request.UpdateInfoRequest;
import com.leteatgo.domain.member.dto.response.MemberProfileResponse;
import com.leteatgo.domain.member.dto.response.MemberMeetingsResponse;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.exception.MemberException;
import com.leteatgo.domain.member.repository.MemberRepository;
import com.leteatgo.domain.member.type.SearchType;
import com.leteatgo.global.dto.CustomPageRequest;
import com.leteatgo.global.storage.FileDto;
import com.leteatgo.global.storage.StorageService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final StorageService storageService;
    private final MeetingParticipantRepository meetingParticipantRepository;

    public MemberProfileResponse getProfile(Long memberId) {
        Member member = getMemberOrThrow(memberId);
        validateMember(member);

        return MemberProfileResponse.fromEntity(member);
    }

    @Transactional
    public void updateInfo(UpdateInfoRequest request, MultipartFile profile,
            Long memberId) {
        Member member = getMemberOrThrow(memberId);
        validateMember(member);

        member.updateInfo(request.nickname(), request.introduce());

        if (!ObjectUtils.isEmpty(profile)) {
            // 기존 프로필 삭제
            storageService.deleteFile(member.getProfileFilename());
            FileDto fileDto = storageService.uploadFile(profile);
            member.addProfile(fileDto.getUrl(), fileDto.getFilename());
        }
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = getMemberOrThrow(memberId);
        validateMember(member);
        member.setDeletedAt(LocalDateTime.now());
        storageService.deleteFile(member.getProfileFilename());
    }

    private static void validateMember(Member member) {
        if (!ObjectUtils.isEmpty(member.getDeletedAt())) {
            throw new MemberException(ALREADY_DELETED_MEMBER);
        }
    }

    public Slice<MemberMeetingsResponse> memberMeetings(SearchType searchType,
            CustomPageRequest request, Long memberId) {
        Member member = getMemberOrThrow(memberId);
        return meetingParticipantRepository.findAllMyMeetings(member, searchType,
                PageRequest.of(request.page(), CustomPageRequest.PAGE_SIZE));
    }

    private Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }
}
