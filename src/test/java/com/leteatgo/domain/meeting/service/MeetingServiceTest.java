package com.leteatgo.domain.meeting.service;

import static com.leteatgo.global.exception.ErrorCode.ALREADY_CANCELED_MEETING;
import static com.leteatgo.global.exception.ErrorCode.ALREADY_COMPLETED_MEETING;
import static com.leteatgo.global.exception.ErrorCode.CANNOT_CANCEL_MEETING;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEETING;
import static com.leteatgo.global.exception.ErrorCode.NOT_MEETING_HOST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.leteatgo.domain.chat.event.ChatRoomEventPublisher;
import com.leteatgo.domain.chat.event.dto.CloseChatRoomEvent;
import com.leteatgo.domain.chat.event.dto.CreateChatRoomEvent;
import com.leteatgo.domain.meeting.dto.request.MeetingCreateRequest;
import com.leteatgo.domain.meeting.dto.request.MeetingOptionsRequest;
import com.leteatgo.domain.meeting.dto.request.MeetingUpdateRequest;
import com.leteatgo.domain.meeting.dto.request.TastyRestaurantRequest;
import com.leteatgo.domain.meeting.dto.response.MeetingCreateResponse;
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
import com.leteatgo.domain.member.repository.MemberRepository;
import com.leteatgo.domain.member.type.LoginType;
import com.leteatgo.domain.member.type.MemberRole;
import com.leteatgo.domain.region.entity.Region;
import com.leteatgo.domain.region.repository.RegionRepository;
import com.leteatgo.domain.tastyrestaurant.entity.TastyRestaurant;
import com.leteatgo.domain.tastyrestaurant.repository.TastyRestaurantRepository;
import com.leteatgo.global.type.RestaurantCategory;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    RegionRepository regionRepository;
    @Mock
    MeetingRepository meetingRepository;
    @Mock
    TastyRestaurantRepository tastyRestaurantRepository;
    @Mock
    ChatRoomEventPublisher chatRoomEventPublisher;
    @InjectMocks
    MeetingService meetingService;

    @Nested
    @DisplayName("createMeeting 메서드는")
    class createdMeetingMethod {

        Member mockMember = createTestMember(1L, "test@naver.com", "testnick", "1!qweqwe",
                "01012345678", LoginType.LOCAL, MemberRole.ROLE_USER);
        Region region = new Region("강남구");
        MeetingOptionsRequest options = new MeetingOptionsRequest(GenderPreference.ANY,
                AgePreference.ANY, MeetingPurpose.DRINKING, AlcoholPreference.ANY);
        TastyRestaurantRequest restaurant = new TastyRestaurantRequest("식당 이름", "123123123", "한식",
                "01012341234", "도로명 주소", "지번 주소", 37.123456, 127.123456, "식당 URL");
        MeetingCreateRequest requestContainRestaurant = MeetingCreateRequest.builder()
                .name("모임 제목")
                .category("한식")
                .region("강남구")
                .maxParticipants(4)
                .minParticipants(2)
                .startDate(LocalDate.of(2024, 1, 31))
                .startTime(LocalTime.of(19, 0))
                .description("모임 설명")
                .options(options)
                .restaurant(restaurant)
                .build();

        MeetingCreateRequest requestNotContainRestaurant = MeetingCreateRequest.builder()
                .name("모임 제목")
                .category("한식")
                .region("강남구")
                .maxParticipants(4)
                .minParticipants(2)
                .startDate(LocalDate.of(2024, 1, 31))
                .startTime(LocalTime.of(19, 0))
                .description("모임 설명")
                .options(options)
                .build();
        @Captor
        ArgumentCaptor<CreateChatRoomEvent> chatRoomEventCaptor;

        @Test
        @DisplayName("[모임 장소를 선택] 처음 선택한 식당이면 식당을 생성하고 방문 횟수를 1로 설정한다.")
        void createMeetingWithRestaurant() {
            // given
            given(memberRepository.findById(mockMember.getId())).willReturn(
                    Optional.of(mockMember));
            given(regionRepository.findByName(region.getName())).willReturn(Optional.of(region));
            given(tastyRestaurantRepository.findByApiId(Long.parseLong(restaurant.apiId())))
                    .willReturn(Optional.empty());

            TastyRestaurant newTastyRestaurant = TastyRestaurantRequest.toEntity(restaurant);
            given(tastyRestaurantRepository.save(any(TastyRestaurant.class))).willReturn(
                    newTastyRestaurant);

            Meeting meeting = MeetingCreateRequest.toEntity(mockMember, region,
                    requestContainRestaurant);
            given(meetingRepository.save(any(Meeting.class))).willReturn(meeting);

            // when
            MeetingCreateResponse response = meetingService.createMeeting(mockMember.getId(),
                    requestContainRestaurant);

            // then
            assertThat(response.id()).isEqualTo(meeting.getId());
            assertThat(newTastyRestaurant.getNumberOfUses()).isEqualTo(1);
            verify(chatRoomEventPublisher, times(1)).publishCreateChatRoom(
                    chatRoomEventCaptor.capture());

            CreateChatRoomEvent createdChatRoom = chatRoomEventCaptor.getValue();
            assertThat(createdChatRoom.meetingId()).isEqualTo(meeting.getId());
        }

        @Test
        @DisplayName("[모임 장소를 선택] 이미 다른 모임에서 선택한 식당이면 해당 식당의 방문 횟수를 증가시킨다.")
        void createMeetingWithAlreadySelectedRestaurant() {
            // given
            given(memberRepository.findById(mockMember.getId())).willReturn(
                    Optional.of(mockMember));
            given(regionRepository.findByName(region.getName())).willReturn(Optional.of(region));

            TastyRestaurant existingTastyRestaurant = TastyRestaurantRequest.toEntity(restaurant);
            given(tastyRestaurantRepository.findByApiId(Long.parseLong(restaurant.apiId())))
                    .willReturn(Optional.of(existingTastyRestaurant));

            Meeting meeting = MeetingCreateRequest.toEntity(mockMember, region,
                    requestContainRestaurant);
            given(meetingRepository.save(any(Meeting.class))).willReturn(meeting);

            // when
            MeetingCreateResponse response = meetingService.createMeeting(mockMember.getId(),
                    requestContainRestaurant);

            // then
            assertThat(response.id()).isEqualTo(meeting.getId());
            assertThat(existingTastyRestaurant.getNumberOfUses()).isEqualTo(2);
            verify(chatRoomEventPublisher, times(1)).publishCreateChatRoom(
                    chatRoomEventCaptor.capture());

            CreateChatRoomEvent createdChatRoomEvent = chatRoomEventCaptor.getValue();
            assertThat(createdChatRoomEvent.meetingId()).isEqualTo(meeting.getId());
        }

        @Test
        @DisplayName("[모임 장소를 선택하지 않음] 모임 장소를 선택하지 않아도 모임을 생성할 수 있다.")
        void createMeetingWithoutRestaurant() {
            // given
            given(memberRepository.findById(mockMember.getId())).willReturn(
                    Optional.of(mockMember));
            given(regionRepository.findByName(region.getName())).willReturn(Optional.of(region));

            Meeting meeting = MeetingCreateRequest.toEntity(mockMember, region,
                    requestNotContainRestaurant);
            given(meetingRepository.save(any(Meeting.class))).willReturn(meeting);

            // when
            MeetingCreateResponse response = meetingService.createMeeting(mockMember.getId(),
                    requestNotContainRestaurant);

            // then
            assertThat(response.id()).isEqualTo(meeting.getId());
            verify(chatRoomEventPublisher, times(1)).publishCreateChatRoom(
                    chatRoomEventCaptor.capture());

            CreateChatRoomEvent createdChatRoomEvent = chatRoomEventCaptor.getValue();
            assertThat(createdChatRoomEvent.meetingId()).isEqualTo(meeting.getId());
        }

    }

    @Nested
    @DisplayName("updateMeeting 메서드는")
    class updatedMeetingMethod {

        Member mockMember = createTestMember(1L, "test@naver.com", "testnick", "1!qweqwe",
                "01012345678", LoginType.LOCAL, MemberRole.ROLE_USER);

        TastyRestaurantRequest restaurant = new TastyRestaurantRequest("식당 이름", "123123123", "한식",
                "01012341234", "도로명 주소", "지번 주소", 37.123456, 127.123456, "식당 URL");

        MeetingOptions options = MeetingOptionsRequest.toEntiy(
                new MeetingOptionsRequest(GenderPreference.ANY, AgePreference.ANY,
                        MeetingPurpose.DRINKING, AlcoholPreference.ANY));
        Meeting existingMeeting = Meeting.builder()
                .host(mockMember)
                .name("모임 제목")
                .restaurantCategory(RestaurantCategory.from("한식"))
                .region(new Region("강남구"))
                .maxParticipants(4)
                .minParticipants(2)
                .startDate(LocalDate.of(2024, 1, 31))
                .startTime(LocalTime.of(19, 0))
                .description("모임 설명")
                .meetingOptions(options)
                .build();

        MeetingUpdateRequest requestContainRestaurant = MeetingUpdateRequest.builder()
                .startDate(LocalDate.of(2024, 2, 1))
                .startTime(LocalTime.of(20, 0))
                .restaurant(restaurant)
                .build();

        MeetingUpdateRequest requestNotContainRestaurant = MeetingUpdateRequest.builder()
                .startDate(LocalDate.of(2024, 2, 1))
                .startTime(LocalTime.of(20, 0))
                .build();

        @Test
        @DisplayName("[모임 장소를 선택] 처음 선택한 식당이면 식당을 생성하고 방문 횟수를 1로 설정한다.")
        void updateMeetingWithRestaurant() {
            // given
            given(meetingRepository.findById(existingMeeting.getId())).willReturn(
                    Optional.of(existingMeeting));
            given(tastyRestaurantRepository.findByApiId(Long.parseLong(restaurant.apiId())))
                    .willReturn(Optional.empty());

            TastyRestaurant newTastyRestaurant = TastyRestaurantRequest.toEntity(restaurant);
            given(tastyRestaurantRepository.save(any(TastyRestaurant.class))).willReturn(
                    newTastyRestaurant);

            // when
            meetingService.updateMeeting(mockMember.getId(), existingMeeting.getId(),
                    requestContainRestaurant);

            // then
            assertThat(newTastyRestaurant.getNumberOfUses()).isEqualTo(1);
            verify(meetingRepository, times(1)).save(existingMeeting);
        }

        @Test
        @DisplayName("[모임 장소를 선택] 이미 다른 모임에서 선택한 식당이면 해당 식당의 방문 횟수를 증가시킨다.")
        void updateMeetingWithAlreadySelectedRestaurant() {
            // given
            given(meetingRepository.findById(existingMeeting.getId())).willReturn(
                    Optional.of(existingMeeting));

            TastyRestaurant existingTastyRestaurant = TastyRestaurantRequest.toEntity(restaurant);
            given(tastyRestaurantRepository.findByApiId(Long.parseLong(restaurant.apiId())))
                    .willReturn(Optional.of(existingTastyRestaurant));

            // when
            meetingService.updateMeeting(mockMember.getId(), existingMeeting.getId(),
                    requestContainRestaurant);

            // then
            assertThat(existingTastyRestaurant.getNumberOfUses()).isEqualTo(2);
            verify(meetingRepository, times(1)).save(existingMeeting);
        }

        @Test
        @DisplayName("[모임 장소를 선택하지 않음] 모임 시간만 수정할 수 있다.")
        void updateMeetingWithoutRestaurant() {
            // given
            given(meetingRepository.findById(existingMeeting.getId())).willReturn(
                    Optional.of(existingMeeting));

            // when
            meetingService.updateMeeting(mockMember.getId(), existingMeeting.getId(),
                    requestNotContainRestaurant);

            // then
            verify(meetingRepository, times(1)).save(existingMeeting);
            assertThat(existingMeeting.getStartDate()).isEqualTo(
                    requestNotContainRestaurant.startDate());
            assertThat(existingMeeting.getStartTime()).isEqualTo(
                    requestNotContainRestaurant.startTime());
        }

        @Test
        @DisplayName("[실패] 모임 주최자가 아니면 모임을 수정할 수 없다.")
        void updateMeetingWithNotHost() {
            // given
            Member notHost = createTestMember(2L, "test2@naver.com", "testnick2", "1!qweqwe",
                    "01012345678", LoginType.LOCAL, MemberRole.ROLE_USER);
            given(meetingRepository.findById(existingMeeting.getId())).willReturn(
                    Optional.of(existingMeeting));
            // when
            // then
            assertThatThrownBy(() -> meetingService.updateMeeting(notHost.getId(),
                    existingMeeting.getId(), requestContainRestaurant))
                    .isInstanceOf(MeetingException.class)
                    .hasMessageContaining(NOT_MEETING_HOST.getErrorMessage());
        }
    }

    @Nested
    @DisplayName("cancelMeeting 메서드는")
    class canceledMeetingMethod {

        Member mockMember = createTestMember(1L, "test@naver.com", "testnick", "1!qweqwe",
                "01012345678", LoginType.LOCAL, MemberRole.ROLE_USER);

        MeetingOptions options = MeetingOptionsRequest.toEntiy(
                new MeetingOptionsRequest(GenderPreference.ANY, AgePreference.ANY,
                        MeetingPurpose.DRINKING, AlcoholPreference.ANY));
        Meeting existingMeeting = Meeting.builder()
                .host(mockMember)
                .name("모임 제목")
                .restaurantCategory(RestaurantCategory.from("한식"))
                .region(new Region("강남구"))
                .maxParticipants(4)
                .minParticipants(2)
                .startDate(LocalDate.of(2024, 1, 31))
                .startTime(LocalTime.of(19, 0))
                .description("모임 설명")
                .meetingOptions(options)
                .build();

        @Captor
        ArgumentCaptor<CloseChatRoomEvent> chatRoomEventCaptor;

        @Test
        @DisplayName("[성공] 모임을 취소할 수 있다.")
        void cancelMeeting() {
            // given
            given(meetingRepository.findById(existingMeeting.getId())).willReturn(
                    Optional.of(existingMeeting));

            // when
            meetingService.cancelMeeting(mockMember.getId(), existingMeeting.getId());

            // then
            verify(meetingRepository, times(1)).save(existingMeeting);
            assertThat(existingMeeting.getMeetingOptions().getStatus()).isEqualTo(
                    MeetingStatus.CANCELED);
            verify(chatRoomEventPublisher, times(1)).publishCloseChatRoom(
                    chatRoomEventCaptor.capture());

            CloseChatRoomEvent closedChatRoomEvent = chatRoomEventCaptor.getValue();
            assertThat(closedChatRoomEvent.meetingId()).isEqualTo(existingMeeting.getId());

        }

        @Test
        @DisplayName("[실패] 모임이 존재하지 않으면 취소할 수 없다.")
        void cancelNotExistingMeeting() {
            // given
            given(meetingRepository.findById(existingMeeting.getId())).willReturn(
                    Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> meetingService.cancelMeeting(mockMember.getId(),
                    existingMeeting.getId()))
                    .isInstanceOf(MeetingException.class)
                    .hasMessageContaining(NOT_FOUND_MEETING.getErrorMessage());
        }

        @Test
        @DisplayName("[실패] 모임 주최자가 아니면 모임을 취소할 수 없다.")
        void cancelMeetingWithNotHost() {
            // given
            Member notHost = createTestMember(2L, "test@naver.com", "testnick", "1!qweqwe",
                    "01012345678", LoginType.LOCAL, MemberRole.ROLE_USER);
            given(meetingRepository.findById(existingMeeting.getId())).willReturn(
                    Optional.of(existingMeeting));

            // when
            // then
            assertThatThrownBy(() -> meetingService.cancelMeeting(notHost.getId(),
                    existingMeeting.getId()))
                    .isInstanceOf(MeetingException.class)
                    .hasMessageContaining(NOT_MEETING_HOST.getErrorMessage());
        }

        @Test
        @DisplayName("[실패] 이미 취소된 모임은 취소할 수 없다.")
        void cancelCanceledMeeting() {
            // given
            existingMeeting.cancel();
            given(meetingRepository.findById(existingMeeting.getId())).willReturn(
                    Optional.of(existingMeeting));

            // when
            // then
            assertThatThrownBy(() -> meetingService.cancelMeeting(mockMember.getId(),
                    existingMeeting.getId()))
                    .isInstanceOf(MeetingException.class)
                    .hasMessageContaining(ALREADY_CANCELED_MEETING.getErrorMessage());
        }

        @Test
        @DisplayName("[실패] 이미 완료된 모임은 취소할 수 없다.")
        void cancelCompletedMeeting() {
            // given
            existingMeeting.complete();
            given(meetingRepository.findById(existingMeeting.getId())).willReturn(
                    Optional.of(existingMeeting));

            // when
            // then
            assertThatThrownBy(() -> meetingService.cancelMeeting(mockMember.getId(),
                    existingMeeting.getId()))
                    .isInstanceOf(MeetingException.class)
                    .hasMessageContaining(ALREADY_COMPLETED_MEETING.getErrorMessage());
        }

        @Test
        @DisplayName("[실패] 모임 시작 1시간 전까지만 취소할 수 있다.")
        void cancelMeetingBeforeOneHour() {
            // given
            existingMeeting.update(LocalDate.now(), LocalTime.now().plusMinutes(59));
            given(meetingRepository.findById(existingMeeting.getId())).willReturn(
                    Optional.of(existingMeeting));
            // when
            // then
            assertThatThrownBy(() -> meetingService.cancelMeeting(mockMember.getId(),
                    existingMeeting.getId()))
                    .isInstanceOf(MeetingException.class)
                    .hasMessageContaining(CANNOT_CANCEL_MEETING.getErrorMessage());
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