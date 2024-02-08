package com.leteatgo.domain.review.event;

import com.leteatgo.domain.review.event.dto.ReviewParticipantEvent;
import com.leteatgo.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewEventHandler {

    private final ReviewService reviewService;

    @EventListener
    public void handleReviewParticipant(ReviewParticipantEvent event) {
        reviewService.reviewParticipant(event.request(), event.reviewerId());
    }
}
