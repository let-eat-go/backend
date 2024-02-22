package com.leteatgo.domain.review.dto.response;

import java.util.List;
import lombok.Builder;

public record ReviewParticipantResponse(
        List<ParticipantResponse> participants
) {

    @Builder
    public record ParticipantResponse(
            Long id,
            String nickname,
            String profileImageUrl,
            boolean isReviewed
    ) {

    }
}
