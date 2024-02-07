package com.leteatgo.domain.member.controller;

import com.leteatgo.domain.member.dto.request.UpdateInfoRequest;
import com.leteatgo.domain.member.dto.response.MemberProfileResponse;
import com.leteatgo.domain.member.dto.response.MyMeetingsResponse;
import com.leteatgo.domain.member.service.MemberService;
import com.leteatgo.domain.member.type.SearchType;
import com.leteatgo.global.dto.CustomPageRequest;
import com.leteatgo.global.dto.SliceResponse;
import com.leteatgo.global.security.annotation.RoleUser;
import com.leteatgo.global.validator.annotation.ValidFile;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RequestMapping("/api/members")
@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    /**
     * 내 정보 조회
     *
     * @param userDetails 인증 유저
     * @return 프로필 정보
     */
    @RoleUser
    @GetMapping("/me")
    public ResponseEntity<MemberProfileResponse> myInformation(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(memberService.getProfile(
                Long.parseLong(userDetails.getUsername())));
    }

    /**
     * 회원 수정
     *
     * @param request     수정 데이터
     * @param profile     프로필 이미지 파일
     * @param userDetails 인증 유저
     * @return void
     */
    @RoleUser
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateInfo(
            @RequestPart @Valid UpdateInfoRequest request,
            @RequestPart(required = false) @ValidFile MultipartFile profile,
            @AuthenticationPrincipal UserDetails userDetails) {
        memberService.updateInfo(request, profile, Long.parseLong(userDetails.getUsername()));
        return ResponseEntity.ok().build();
    }

    /**
     * 회원 삭제
     *
     * @param userDetails 인증 유저
     * @return void
     */
    @RoleUser
    @DeleteMapping
    public ResponseEntity<Void> deleteMember(
            @AuthenticationPrincipal UserDetails userDetails) {
        memberService.deleteMember(Long.parseLong(userDetails.getUsername()));
        return ResponseEntity.ok().build();
    }

    /**
     * 내 모임 목록 조회
     *
     * @param type        조회 타입(모임 예정, 모임 완료, 내가 생성한 모임)
     * @param request     요청 페이지
     * @param userDetails 인증 유저
     * @return 모임 목록
     */
    @RoleUser
    @GetMapping("/meetings/me")
    public ResponseEntity<SliceResponse<MyMeetingsResponse>> myMeetings(
            @RequestParam SearchType type,
            @Valid CustomPageRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(new SliceResponse<>(
                memberService.myMeetings(type, request,
                        Long.parseLong(userDetails.getUsername()))));
    }

    /**
     * 타 회원 조회
     *
     * @param memberId 타 회원 id
     * @return 프로필 정보
     */
    @RoleUser
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberProfileResponse> memberProfile(
            @PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getProfile(memberId));
    }
}
