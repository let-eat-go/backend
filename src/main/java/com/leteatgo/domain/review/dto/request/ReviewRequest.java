package com.leteatgo.domain.review.dto.request;

import static com.leteatgo.global.constants.DtoValid.EMPTY_MESSAGE;

import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.review.entity.Review;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ReviewRequest(
        @NotNull(message = EMPTY_MESSAGE)
        Long meetingId,

        @NotNull(message = EMPTY_MESSAGE)
        Long revieweeId,

        @NotNull(message = EMPTY_MESSAGE)
        Double score
) {

    public Review toEntity(Member reviewer, Member reviewee, Meeting meeting) {
        return Review.builder()
                .score(score)
                .reviewer(reviewer)
                .reviewee(reviewee)
                .meeting(meeting)
                .build();
    }
}
