package com.leteatgo.domain.meeting.controller;

import com.leteatgo.domain.meeting.dto.request.MeetingCreateRequest;
import com.leteatgo.domain.meeting.dto.response.MeetingCreateResponse;
import com.leteatgo.domain.meeting.service.MeetingService;
import com.leteatgo.global.security.annotation.RoleUser;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    // 모임 생성
    @PostMapping()
    @RoleUser
    public ResponseEntity<Void> createMeeting(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid MeetingCreateRequest request
    ) {
        MeetingCreateResponse response = meetingService.createMeeting(
                Long.parseLong(userDetails.getUsername()), request);
        URI location = UriComponentsBuilder.fromUriString("/api/meetings/" + response.id())
                .build().toUri();
        return ResponseEntity.created(location).build();
    }
}
