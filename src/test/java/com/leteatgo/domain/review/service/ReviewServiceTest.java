package com.leteatgo.domain.review.service;

import static com.leteatgo.global.exception.ErrorCode.ALREADY_REVIEWED;
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

import com.leteatgo.domain.meeting.dto.request.MeetingCancelRequest;
import com.leteatgo.domain.meeting.dto.request.MeetingOptionsRequest;
import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.entity.MeetingOptions;
import com.leteatgo.domain.meeting.exception.MeetingException;
import com.leteatgo.domain.meeting.repository.MeetingRepository;
import com.leteatgo.domain.meeting.type.AgePreference;
import com.leteatgo.domain.meeting.type.AlcoholPreference;
import com.leteatgo.domain.meeting.type.GenderPreference;
import com.leteatgo.domain.meeting.type.MeetingPurpose;
import com.leteatgo.domain.meeting.type.MeetingStatus;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.exception.MemberException;
import com.leteatgo.domain.member.type.LoginType;
import com.leteatgo.domain.member.type.MemberRole;
import com.leteatgo.domain.region.entity.Region;
import com.leteatgo.domain.review.dto.request.ReviewRequest;
import com.leteatgo.domain.review.dto.response.ReviewParticipantResponse;
import com.leteatgo.domain.review.entity.Review;
import com.leteatgo.domain.review.exception.ReviewException;
import com.leteatgo.domain.review.repository.ReviewRepository;
import com.leteatgo.global.security.CustomUserDetailService;
import com.leteatgo.global.type.RestaurantCategory;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
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

        @Test
        @DisplayName("실패 - 이미 평가한 모임원이면 예외가 발생한다.")
        void reviewParticipant_already_reviewed() {
            // given
            given(userDetailService.findByIdOrThrow(reviewerId))
                    .willReturn(reviewer);

            given(userDetailService.findByIdOrThrow(revieweeId))
                    .willReturn(reviewee);

            given(meetingRepository.findMeetingFetch(meetingId))
                    .willReturn(Optional.of(meeting));

            given(reviewRepository.existsByReviewerIdAndRevieweeIdAndMeetingId(
                    reviewerId, revieweeId, meeting.getId()))
                    .willReturn(true);

            // when
            // then
            assertThatThrownBy(() -> reviewService.reviewParticipant(request, reviewerId))
                    .isInstanceOf(ReviewException.class)
                    .hasMessageContaining(ALREADY_REVIEWED.getErrorMessage());
        }
    }

    @Nested
    @DisplayName("평가할 모임원 조회 메서드")
    class GetReviewParticipantMethod {

        Member host = createTestMember(1L, "host@naver.com", "host", "1!qweqwe",
                "01012345678", LoginType.LOCAL, MemberRole.ROLE_USER);
        Member member1 = createTestMember(2L, "test@naver.com", "testnick", "1!qweqwe",
                "01012345678", LoginType.LOCAL, MemberRole.ROLE_USER);
        Member member2 = createTestMember(3L, "test2@naver.com", "testnick2", "1!qweqwe",
                "01012345671", LoginType.LOCAL, MemberRole.ROLE_USER);
        MeetingOptions options = MeetingOptionsRequest.toEntiy(
                new MeetingOptionsRequest(GenderPreference.ANY, AgePreference.ANY,
                        MeetingPurpose.DRINKING, AlcoholPreference.ANY));
        Meeting existingMeeting = Meeting.builder()
                .host(host)
                .name("모임 제목")
                .restaurantCategory(RestaurantCategory.from("한식"))
                .region(new Region("강남구"))
                .maxParticipants(2)
                .minParticipants(2)
                .startDateTime(LocalDateTime.of(2025, 1, 31, 19, 0))
                .description("모임 설명")
                .meetingOptions(options)
                .build();
        Long reviewerId = 1L;
        Long meetingId = 1L;

        @Test
        @DisplayName("자기 자신을 제외한 모임원을 조회한다.")
        void getReviewParticipant() {
            // given
            existingMeeting.addMeetingParticipant(member1);
            existingMeeting.addMeetingParticipant(member2);
            given(meetingRepository.findMeetingFetch(meetingId))
                    .willReturn(Optional.of(existingMeeting));

            Set<Long> participantIds = Set.of(member1.getId(), member2.getId());
            given(reviewRepository.findReviewedMemberIds(reviewerId, meetingId))
                    .willReturn(participantIds);

            // when
            ReviewParticipantResponse response = reviewService.getReviewParticipant(reviewerId,
                    meetingId);

            // then
            assertEquals(2, response.participants().size());
            // 2,3번 모임원이 조회되어야 한다.
            assertEquals(Set.of(2L, 3L), participantIds);
        }

        @Test
        @DisplayName("실패 - 모임을 찾을 수 없으면 예외가 발생한다.")
        void getReviewParticipant_not_found_meeting() {
            // given
            given(meetingRepository.findMeetingFetch(meetingId))
                    .willReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> reviewService.getReviewParticipant(reviewerId, meetingId))
                    .isInstanceOf(MeetingException.class)
                    .hasMessageContaining(NOT_FOUND_MEETING.getErrorMessage());
        }
    }

    private Member createTestMember(Long id, String email, String nickname, String password,
            String phoneNumber, LoginType loginType, MemberRole role) {
        Member member = Member.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .phoneNumber(phoneNumber)
                .loginType(loginType)
                .role(role)
                .build();

        ReflectionTestUtils.setField(member, "id", id);

        return member;
    }
}