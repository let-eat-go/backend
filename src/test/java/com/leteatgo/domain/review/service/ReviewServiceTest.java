package com.leteatgo.domain.review.service;

import static com.leteatgo.global.exception.ErrorCode.NOT_COMPLETED_MEETING;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEETING;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEMBER;
import static com.leteatgo.global.exception.ErrorCode.NOT_JOINED_MEETING;
import static com.leteatgo.global.exception.ErrorCode.NOT_MEETING_PARTICIPANT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.entity.MeetingOptions;
import com.leteatgo.domain.meeting.exception.MeetingException;
import com.leteatgo.domain.meeting.repository.MeetingRepository;
import com.leteatgo.domain.meeting.type.MeetingStatus;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.exception.MemberException;
import com.leteatgo.domain.review.dto.request.ReviewRequest;
import com.leteatgo.domain.review.entity.Review;
import com.leteatgo.domain.review.exception.ReviewException;
import com.leteatgo.domain.review.repository.ReviewRepository;
import com.leteatgo.global.security.CustomUserDetailService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    CustomUserDetailService userDetailService;

    @Mock
    MeetingRepository meetingRepository;

    @InjectMocks
    ReviewService reviewService;

    @Nested
    @DisplayName("평가하기 메서드")
    class ReviewParticipantMethod {

        Long reviewerId = 1L;
        Long revieweeId = 2L;
        Long meetingId = 1L;
        Double score = -1.0;
        ReviewRequest request = ReviewRequest.builder()
                .meetingId(meetingId)
                .revieweeId(revieweeId)
                .score(score)
                .build();

        Member reviewer = Member.builder().build();
        Member reviewee = Member.builder().build();
        Meeting meeting = Meeting.builder()
                .meetingOptions(MeetingOptions.builder()
                        .status(MeetingStatus.COMPLETED)
                        .build())
                .build();
        Review review = Review.builder()
                .score(score)
                .reviewer(reviewer)
                .reviewee(reviewee)
                .meeting(meeting)
                .build();

        @BeforeEach
        void setup() {
            ReflectionTestUtils.setField(reviewer, "id", reviewerId);
            ReflectionTestUtils.setField(reviewee, "id", revieweeId);

            meeting.addMeetingParticipant(reviewer);
            meeting.addMeetingParticipant(reviewee);
        }

        @Test
        @DisplayName("성공")
        void reviewParticipant() {
            // given
            given(userDetailService.findByIdOrThrow(reviewerId))
                    .willReturn(reviewer);

            given(userDetailService.findByIdOrThrow(revieweeId))
                    .willReturn(reviewee);

            given(meetingRepository.findMeetingFetch(meetingId))
                    .willReturn(Optional.of(meeting));

            given(reviewRepository.save(any()))
                    .willReturn(review);
            ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);

            // when
            reviewService.reviewParticipant(request, reviewerId);

            // then
            verify(reviewRepository, times(1)).save(captor.capture());
            assertEquals(35.5, reviewee.getMannerTemperature());
        }

        @Test
        @DisplayName("실패 - 평가자를 찾을 수 없으면 예외가 발생한다.")
        void reviewParticipant_not_found_reviewer() {
            // given
            given(userDetailService.findByIdOrThrow(reviewerId))
                    .willThrow(new MemberException(NOT_FOUND_MEMBER));

            // when
            // then
            assertThatThrownBy(() -> reviewService.reviewParticipant(request, reviewerId))
                    .isInstanceOf(MemberException.class)
                    .hasMessageContaining(NOT_FOUND_MEMBER.getErrorMessage());
        }

        @Test
        @DisplayName("실패 - 평가 받는자를 찾을 수 없으면 예외가 발생한다.")
        void reviewParticipant_not_found_reviewee() {
            // given
            given(userDetailService.findByIdOrThrow(reviewerId))
                    .willReturn(reviewer);

            given(userDetailService.findByIdOrThrow(revieweeId))
                    .willThrow(new MemberException(NOT_FOUND_MEMBER));

            // when
            // then
            assertThatThrownBy(() -> reviewService.reviewParticipant(request, reviewerId))
                    .isInstanceOf(MemberException.class)
                    .hasMessageContaining(NOT_FOUND_MEMBER.getErrorMessage());
        }

        @Test
        @DisplayName("실패 - 모임을 찾을 수 없으면 예외가 발생한다.")
        void reviewParticipant_not_found_meeting() {
            // given
            given(userDetailService.findByIdOrThrow(reviewerId))
                    .willReturn(reviewer);

            given(userDetailService.findByIdOrThrow(revieweeId))
                    .willReturn(reviewee);

            given(meetingRepository.findMeetingFetch(meetingId))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> reviewService.reviewParticipant(request, reviewerId))
                    .isInstanceOf(MeetingException.class)
                    .hasMessageContaining(NOT_FOUND_MEETING.getErrorMessage());
        }

        @Test
        @DisplayName("실패 - 평가자가 모임 참가자가 아니면 예외가 발생한다.")
        void reviewParticipant_cannot_access_reviewer() {
            // given
            meeting.removeMeetingParticipant(meeting.getMeetingParticipants().get(0));

            given(userDetailService.findByIdOrThrow(reviewerId))
                    .willReturn(reviewer);

            given(userDetailService.findByIdOrThrow(revieweeId))
                    .willReturn(reviewee);

            given(meetingRepository.findMeetingFetch(meetingId))
                    .willReturn(Optional.of(meeting));

            // when
            // then
            assertThatThrownBy(() -> reviewService.reviewParticipant(request, reviewerId))
                    .isInstanceOf(ReviewException.class)
                    .hasMessageContaining(NOT_JOINED_MEETING.getErrorMessage());
        }

        @Test
        @DisplayName("실패 - 평가 받는자가 모임 참가자가 아니면 예외가 발생한다.")
        void reviewParticipant_cannot_access_reviewee() {
            // given
            meeting.removeMeetingParticipant(meeting.getMeetingParticipants().get(1));

            given(userDetailService.findByIdOrThrow(reviewerId))
                    .willReturn(reviewer);

            given(userDetailService.findByIdOrThrow(revieweeId))
                    .willReturn(reviewee);

            given(meetingRepository.findMeetingFetch(meetingId))
                    .willReturn(Optional.of(meeting));

            // when
            // then
            assertThatThrownBy(() -> reviewService.reviewParticipant(request, reviewerId))
                    .isInstanceOf(ReviewException.class)
                    .hasMessageContaining(NOT_MEETING_PARTICIPANT.getErrorMessage());
        }

        @Test
        @DisplayName("실패 - 완료된 모임이 아니면 예외가 발생한다.")
        void reviewParticipant_not_completed_meeting() {
            // given
            meeting.inProgress();

            given(userDetailService.findByIdOrThrow(reviewerId))
                    .willReturn(reviewer);

            given(userDetailService.findByIdOrThrow(revieweeId))
                    .willReturn(reviewee);

            given(meetingRepository.findMeetingFetch(meetingId))
                    .willReturn(Optional.of(meeting));

            // when
            // then
            assertThatThrownBy(() -> reviewService.reviewParticipant(request, reviewerId))
                    .isInstanceOf(ReviewException.class)
                    .hasMessageContaining(NOT_COMPLETED_MEETING.getErrorMessage());
        }
    }
}