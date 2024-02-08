package com.leteatgo.domain.review.event.dto;

import com.leteatgo.domain.review.dto.request.ReviewRequest;

public record ReviewParticipantEvent(
        ReviewRequest request,
        Long reviewerId
) {

}
