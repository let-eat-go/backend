package com.leteatgo.domain.meeting.service;

import static com.leteatgo.global.exception.ErrorCode.ALREADY_FULL_PARTICIPANT;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.entity.MeetingOptions;
import com.leteatgo.domain.meeting.entity.MeetingParticipant;
import com.leteatgo.domain.meeting.repository.MeetingParticipantRepository;
import com.leteatgo.domain.meeting.repository.MeetingRepository;
import com.leteatgo.domain.meeting.type.AgePreference;
import com.leteatgo.domain.meeting.type.AlcoholPreference;
import com.leteatgo.domain.meeting.type.GenderPreference;
import com.leteatgo.domain.meeting.type.MeetingPurpose;
import com.leteatgo.domain.meeting.type.MeetingStatus;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.repository.MemberRepository;
import com.leteatgo.domain.member.type.LoginType;
import com.leteatgo.domain.member.type.MemberRole;
import com.leteatgo.domain.region.entity.Region;
import com.leteatgo.domain.region.repository.RegionRepository;
import com.leteatgo.domain.tastyrestaurant.entity.TastyRestaurant;
import com.leteatgo.domain.tastyrestaurant.repository.TastyRestaurantRepository;
import com.leteatgo.global.type.RestaurantCategory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MeetingConcurrencyTest {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private TastyRestaurantRepository tastyRestaurantRepository;

    @Autowired
    private MeetingParticipantRepository meetingParticipantRepository;

    @BeforeEach
    void setUp() {
        for (int i = 1; i <= 4; i++) {
            memberRepository.saveAndFlush(Member.builder()
                    .email("test" + i + "@naver.com")
                    .nickname("test" + i)
                    .password("1!qweqwe")
                    .phoneNumber("0101234567" + i)
                    .loginType(LoginType.LOCAL)
                    .role(MemberRole.ROLE_USER)
                    .build());
        }

        regionRepository.saveAndFlush(new Region("강남구"));

        tastyRestaurantRepository.saveAndFlush(TastyRestaurant.builder()
                .apiId(111L)
                .name("맛집")
                .category(RestaurantCategory.BUFFET)
                .phoneNumber("01012345678")
                .roadAddress("서울특별시 강남구 테헤란로 427")
                .landAddress("서울특별시 강남구 역삼동 825-25")
                .latitude(37.123456)
                .longitude(127.123456)
                .restaurantUrl("https://www.naver.com")
                .numberOfUses(0)
                .build());

        meetingRepository.saveAndFlush(Meeting.builder()
                .host(memberRepository.findById(1L).get())
                .tastyRestaurant(tastyRestaurantRepository.findById(1L).get())
                .name("테스트 모임")
                .restaurantCategory(RestaurantCategory.BUFFET)
                .region(regionRepository.findById(1L).get())
                .minParticipants(2)
                .maxParticipants(3)
                .startDateTime(LocalDateTime.now().plusDays(1))
                .description("테스트 모임입니다.")
                .meetingOptions(MeetingOptions.builder()
                        .agePreference(AgePreference.ANY)
                        .alcoholPreference(AlcoholPreference.ANY)
                        .genderPreference(GenderPreference.ANY)
                        .purpose(MeetingPurpose.DRINKING)
                        .status(MeetingStatus.BEFORE)
                        .build())
                .build());

        meetingParticipantRepository.saveAndFlush(MeetingParticipant.builder()
                .meeting(meetingRepository.findById(1L).get())
                .member(memberRepository.findById(1L).get())
                .build());
    }

    @Test
    @DisplayName("모임 참가 신청 동시성 테스트")
    void joinMeeting_concurrency() throws InterruptedException {
        // given
        int numberOfThreads = 3;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        Long meetingId = 1L;
        AtomicReference<String> exceptionMessage = new AtomicReference<>();

        // when
        for (long i = 2; i <= 4; i++) { // 2,3,4번 회원이 동시에 모임 참가 신청을 합니다.
            long idx = i;
            service.execute(() -> {
                try {
                    Long memberId = idx;
                    meetingService.joinMeeting(memberId, meetingId);
                } catch (Exception e) {
                    exceptionMessage.set(e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        // then
        latch.await();
        service.shutdown();

        Meeting meeting = meetingRepository.findById(meetingId).get();
        List<MeetingParticipant> allParticipants = meetingParticipantRepository.findAll();
        List<MeetingParticipant> participants = allParticipants.stream()
                .filter(participant -> participant.getMeeting().getId().equals(meetingId))
                .toList();
        assertSoftly(softly -> {
            // meeting의 currentParticipants는 3이어야 합니다. (최대 참가자 수=3)
            softly.assertThat(meeting.getCurrentParticipants()).isEqualTo(3);
            // participants의 size는 3이어야 합니다.
            softly.assertThat(participants.size()).isEqualTo(3);
            // 모임 정원 초과로 참가 신청이 실패했어야 합니다.
            softly.assertThat(exceptionMessage.get())
                    .isEqualTo(ALREADY_FULL_PARTICIPANT.getErrorMessage());
        });
    }

    @Test
    @DisplayName("모임 취소 동시성 테스트 (모임 시작 1시간 이내)")
    void cancelJoinMeeting_concurrency_withinOneHour() throws InterruptedException {
        // given
        int numberOfThreads = 2;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        Long meetingId = 1L;

        // 모임 시작 시간을 현재로부터 1분 후로 변경
        Meeting meeting = meetingRepository.findById(meetingId).get();
        meeting.update(LocalDateTime.now().plusMinutes(1));
        meetingRepository.saveAndFlush(meeting);

        // 참가자를 모임에 추가
        for (long i = 2; i <= 3; i++) {
            Long memberId = i;
            meetingService.joinMeeting(memberId, meetingId);
        }

        // when
        for (long i = 2; i <= 3; i++) { // 2,3번 회원이 동시에 모임을 나갑니다
            long idx = i;
            service.execute(() -> {
                try {
                    Long memberId = idx;
                    meetingService.cancelJoinMeeting(memberId, meetingId);
                } finally {
                    latch.countDown();
                }
            });
        }

        // then
        latch.await();
        service.shutdown();

        List<MeetingParticipant> allParticipants = meetingParticipantRepository.findAll();
        List<MeetingParticipant> participants = allParticipants.stream()
                .filter(participant -> participant.getMeeting().getId().equals(meetingId))
                .toList();
        assertSoftly(softly -> {
            // meeting의 currentParticipants는 1이어야 합니다. (주최자 1명만 남았으므로)
            softly.assertThat(meeting.getCurrentParticipants()).isEqualTo(1);
            // participants의 size는 1이어야 합니다.
            softly.assertThat(participants.size()).isEqualTo(1);
            // 2,3번 회원의 매너온도가 감소했어야 합니다.
            for (long i = 2; i <= 3; i++) {
                Member member = memberRepository.findById(i).get();
                softly.assertThat(member.getMannerTemperature()).isLessThan(36.5);
            }
        });
    }
}
