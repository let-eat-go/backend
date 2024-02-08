package com.leteatgo.domain.review.event;

import com.leteatgo.domain.review.event.dto.ReviewParticipantEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishReviewParticipant(ReviewParticipantEvent event) {
        eventPublisher.publishEvent(event);
    }
}
