package com.leteatgo.domain.member.controller;

import com.leteatgo.domain.member.dto.response.MyInfoResponse;
import com.leteatgo.domain.member.service.MemberService;
import com.leteatgo.global.security.annotation.RoleUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/members")
@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @RoleUser
    @GetMapping("/me")
    public ResponseEntity<MyInfoResponse> myInformation(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                memberService.myInformation(userDetails.getUsername()));
    }


}
