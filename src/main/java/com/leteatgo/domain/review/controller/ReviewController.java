package com.leteatgo.domain.review.controller;

import com.leteatgo.domain.review.dto.request.ReviewRequest;
import com.leteatgo.domain.review.dto.response.ReviewParticipantResponse;
import com.leteatgo.domain.review.service.ReviewService;
import com.leteatgo.global.security.annotation.RoleUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/review")
@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    // 모임원 평가
    @RoleUser
    @PostMapping
    public ResponseEntity<Void> reviewParticipant(
            @RequestBody @Valid ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        reviewService.reviewParticipant(request, Long.parseLong(userDetails.getUsername()));
        return ResponseEntity.ok().build();
    }

    // 평가할 모임원 조회
    @RoleUser
    @GetMapping("/{meetingId}")
    public ResponseEntity<ReviewParticipantResponse> getReviewParticipant(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long meetingId) {
        ReviewParticipantResponse reviewParticipant = reviewService.getReviewParticipant(
                Long.parseLong(userDetails.getUsername()), meetingId);
        return ResponseEntity.ok().body(reviewParticipant);
    }
}
