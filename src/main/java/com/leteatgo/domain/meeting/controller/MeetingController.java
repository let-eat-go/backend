package com.leteatgo.domain.meeting.controller;

import com.leteatgo.domain.meeting.dto.request.MeetingCreateRequest;
import com.leteatgo.domain.meeting.dto.request.MeetingUpdateRequest;
import com.leteatgo.domain.meeting.dto.response.MeetingCreateResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingListResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingSearchResponse;
import com.leteatgo.domain.meeting.service.MeetingService;
import com.leteatgo.global.dto.CustomPageRequest;
import com.leteatgo.global.dto.SliceResponse;
import com.leteatgo.global.security.annotation.RoleUser;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    // 모임 생성
    @PostMapping
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

    // 모임 수정
    @PutMapping("/{meetingId}")
    @RoleUser
    public ResponseEntity<Void> updateMeeting(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long meetingId,
            @RequestBody @Valid MeetingUpdateRequest request
    ) {
        meetingService.updateMeeting(Long.parseLong(userDetails.getUsername()), meetingId, request);
        return ResponseEntity.ok().build();
    }

    // 모임 취소(주최자)
    @DeleteMapping("/{meetingId}/cancel")
    @RoleUser
    public ResponseEntity<Void> cancelMeeting(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long meetingId
    ) {
        meetingService.cancelMeeting(Long.parseLong(userDetails.getUsername()), meetingId);
        return ResponseEntity.ok().build();
    }

    // 모임 상세 조회
    @GetMapping("/detail/{meetingId}")
    public ResponseEntity<MeetingDetailResponse> getMeetingDetail(
            @PathVariable Long meetingId
    ) {
        MeetingDetailResponse response = meetingService.getMeetingDetail(meetingId);
        return ResponseEntity.ok(response);
    }

    // 모임 목록 조회
    @GetMapping("/list")
    public ResponseEntity<SliceResponse<MeetingListResponse>> getMeetingList(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String region,
            @Valid CustomPageRequest request
    ) {
        Slice<MeetingListResponse> response = meetingService.getMeetingList(
                category, region, request);
        return ResponseEntity.ok(new SliceResponse<>(response));
    }

    // 모임 검색
    @GetMapping("/search")
    public ResponseEntity<SliceResponse<MeetingSearchResponse>> searchMeetings(
            @RequestParam String type,
            @RequestParam String term,
            @Valid CustomPageRequest request
    ) {
        Slice<MeetingSearchResponse> response = meetingService.searchMeetings(type, term, request);
        return ResponseEntity.ok(new SliceResponse<>(response));
    }

    // 모임 참여
    @PostMapping("/{meetingId}/join")
    @RoleUser
    public ResponseEntity<Void> joinMeeting(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long meetingId
    ) {
        meetingService.joinMeeting(Long.parseLong(userDetails.getUsername()), meetingId);
        return ResponseEntity.ok().build();
    }

    // 모임 참여 취소
    @DeleteMapping("/{meetingId}/leave")
    @RoleUser
    public ResponseEntity<Void> cancelJoinMeeting(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long meetingId
    ) {
        meetingService.cancelJoinMeeting(Long.parseLong(userDetails.getUsername()), meetingId);
        return ResponseEntity.ok().build();
    }

}
