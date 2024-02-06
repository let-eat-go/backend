package com.leteatgo.domain.member.controller;

import com.leteatgo.domain.member.dto.request.UpdateInfoRequest;
import com.leteatgo.domain.member.dto.response.MyInfoResponse;
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

    @RoleUser
    @GetMapping("/me")
    public ResponseEntity<MyInfoResponse> myInformation(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(memberService.myInformation(
                Long.parseLong(userDetails.getUsername())));
    }

    @RoleUser
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateInfo(
            @RequestPart @Valid UpdateInfoRequest request,
            @RequestPart(required = false) @ValidFile MultipartFile profile,
            @AuthenticationPrincipal UserDetails userDetails) {
        memberService.updateInfo(request, profile, Long.parseLong(userDetails.getUsername()));
        return ResponseEntity.ok().build();
    }

    @RoleUser
    @DeleteMapping
    public ResponseEntity<Void> deleteMember(
            @AuthenticationPrincipal UserDetails userDetails) {
        memberService.deleteMember(Long.parseLong(userDetails.getUsername()));
        return ResponseEntity.ok().build();
    }

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
}
