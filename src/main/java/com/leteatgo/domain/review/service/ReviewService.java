package com.leteatgo.domain.review.service;

import static com.leteatgo.global.exception.ErrorCode.NOT_COMPLETED_MEETING;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEETING;
import static com.leteatgo.global.exception.ErrorCode.NOT_JOINED_MEETING;
import static com.leteatgo.global.exception.ErrorCode.NOT_MEETING_PARTICIPANT;

import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.exception.MeetingException;
import com.leteatgo.domain.meeting.repository.MeetingRepository;
import com.leteatgo.domain.meeting.type.MeetingStatus;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.review.dto.request.ReviewRequest;
import com.leteatgo.domain.review.exception.ReviewException;
import com.leteatgo.domain.review.repository.ReviewRepository;
import com.leteatgo.global.exception.ErrorCode;
import com.leteatgo.global.lock.annotation.DistributedLock;
import com.leteatgo.global.security.CustomUserDetailService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CustomUserDetailService userDetailService;
    private final MeetingRepository meetingRepository;

    @DistributedLock(key = "'reviewParticipant:' + #reviewerId")
    public void reviewParticipant(ReviewRequest request, Long reviewerId) {
        Member reviewer = userDetailService.findByIdOrThrow(reviewerId);
        Member reviewee = userDetailService.findByIdOrThrow(request.revieweeId());

        Meeting meeting = meetingRepository.findMeetingFetch(request.meetingId())
                .orElseThrow(() -> new MeetingException(NOT_FOUND_MEETING));

        checkParticipant(meeting, reviewer.getId(), NOT_JOINED_MEETING);
        checkParticipant(meeting, reviewee.getId(), NOT_MEETING_PARTICIPANT);
        validateMeeting(meeting);

        reviewee.updateMannerTemperature(request.score());
        reviewRepository.save(request.toEntity(reviewer, reviewee, meeting));
    }

    private void checkParticipant(Meeting meeting, Long memberId, ErrorCode errorCode) {
        if (meeting.getMeetingParticipants().stream().noneMatch(o ->
                Objects.equals(o.getMember().getId(), memberId))) {
            throw new ReviewException(errorCode);
        }
    }

    private void validateMeeting(Meeting meeting) {
        if (meeting.getMeetingOptions().getStatus() != MeetingStatus.COMPLETED) {
            throw new ReviewException(NOT_COMPLETED_MEETING);
        }
    }
}
