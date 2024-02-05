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
import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingListResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingSearchResponse;
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
import com.leteatgo.global.dto.CustomPageRequest;
import com.leteatgo.global.type.RestaurantCategory;
import com.leteatgo.global.util.SliceUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
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
                .startDateTime(LocalDateTime.of(2024, 1, 31, 19, 0))
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
            assertThat(existingMeeting.getStartDateTime()).isEqualTo(
                    LocalDateTime.of(requestNotContainRestaurant.startDate(),
                            requestNotContainRestaurant.startTime()));
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
                .startDateTime(LocalDateTime.of(2025, 1, 31, 19, 0))
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
            existingMeeting.update(LocalDateTime.now().plusMinutes(1));
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

    @Nested
    @DisplayName("getMeetingDetail 메서드는")
    class getMeetingDetailMethod {

        MeetingDetailResponse.MeetingResponse meetingResponse() {
            return new MeetingDetailResponse.MeetingResponse(
                    1L,
                    "모임 제목",
                    null,
                    2,
                    4,
                    1,
                    LocalDate.of(2024, 1, 31).atTime(LocalTime.of(19, 0)),
                    "모임 설명",
                    null,
                    GenderPreference.ANY,
                    AgePreference.ANY
            );
        }

        MeetingDetailResponse.HostResponse hostResponse() {
            return new MeetingDetailResponse.HostResponse(
                    1L,
                    "주최자 닉네임",
                    "주최자 프로필 이미지 URL"
            );
        }

        MeetingDetailResponse.ParticipantResponse participantResponse() {
            return new MeetingDetailResponse.ParticipantResponse(
                    2L,
                    "참가자 닉네임",
                    "참가자 프로필 이미지 URL"
            );
        }

        MeetingDetailResponse.RestaurantResponse restaurantResponse() {
            return new MeetingDetailResponse.RestaurantResponse(
                    1L,
                    "식당 이름",
                    "도로명 주소",
                    "01012341234",
                    37.123456,
                    127.123456
            );
        }

        MeetingDetailResponse response = new MeetingDetailResponse(
                meetingResponse(),
                hostResponse(),
                List.of(participantResponse()),
                restaurantResponse(),
                1
        );

        @Test
        @DisplayName("[성공] 모임 상세 정보를 조회할 수 있다.")
        void getMeetingDetail() {
            // given
            Long meetingId = 1L;
            given(meetingRepository.findMeetingDetail(meetingId)).willReturn(
                    Optional.of(response));

            // when
            MeetingDetailResponse response = meetingService.getMeetingDetail(meetingId);

            // then
            assertThat(response).isEqualTo(this.response);
        }

        @Test
        @DisplayName("[실패] 존재하지 않는 Meeting의 상세 정보를 가져오려고 하면 예외가 발생한다.")
        void getMeetingDetailWithNonExistingMeeting() {
            // given
            Long meetingId = 1L;
            given(meetingRepository.findMeetingDetail(meetingId)).willReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> meetingService.getMeetingDetail(meetingId))
                    .isInstanceOf(MeetingException.class)
                    .hasMessageContaining(NOT_FOUND_MEETING.getErrorMessage());
        }

    }

    @Nested
    @DisplayName("getMeetingList 메서드는")
    class getMeetingListMethod {

        MeetingListResponse meetingListResponse() {
            return new MeetingListResponse(
                    1L,
                    "모임 제목",
                    RestaurantCategory.KOREAN_CUISINE,
                    2,
                    4,
                    1,
                    LocalDate.of(2024, 1, 31).atTime(LocalTime.of(19, 0)),
                    LocalDate.of(2024, 1, 30).atTime(LocalTime.of(18, 0)),
                    "모임 설명",
                    MeetingStatus.BEFORE,
                    restaurantResponse()
            );
        }

        MeetingDetailResponse.RestaurantResponse restaurantResponse() {
            return new MeetingDetailResponse.RestaurantResponse(
                    1L,
                    "식당 이름",
                    "도로명 주소",
                    "01012341234",
                    37.123456,
                    127.123456
            );
        }

        List<MeetingListResponse> meetingListResponses = List.of(meetingListResponse());
        CustomPageRequest customPageRequest = new CustomPageRequest(1);
        Slice<MeetingListResponse> response = new SliceUtil<>(meetingListResponses,
                PageRequest.of(0, 10)).getSlice();

        @Test
        @DisplayName("[성공] 모임 목록을 조회할 수 있다.")
        void getMeetingList() {
            // given
            String category = "한식";
            String regionName = "강남구";
            given(meetingRepository.findMeetingList(category, regionName,
                    PageRequest.of(customPageRequest.page(), CustomPageRequest.PAGE_SIZE)))
                    .willReturn(response);

            // when
            Slice<MeetingListResponse> response = meetingService.getMeetingList(category,
                    regionName,
                    customPageRequest);

            // then
            assertThat(response.getContent().size()).isEqualTo(1);
            assertThat(response.getContent().get(0)).isEqualTo(meetingListResponse());
        }
    }

    @Nested
    @DisplayName("searchMeetings 메서드는")
    class searchMeetingsMethod {

        MeetingSearchResponse meetingSearchResponse() {
            return new MeetingSearchResponse(
                    1L,
                    "모임 제목",
                    "식당 이름",
                    "도로명 주소",
                    RestaurantCategory.ASIAN_CUISINE,
                    LocalDate.of(2024, 1, 31).atTime(LocalTime.of(19, 0)),
                    2,
                    4,
                    1,
                    MeetingStatus.BEFORE
            );
        }

        List<MeetingSearchResponse> meetingSearchResponses = List.of(meetingSearchResponse());
        CustomPageRequest pageRequest = new CustomPageRequest(1);
        Slice<MeetingSearchResponse> response = new SliceUtil<>(meetingSearchResponses,
                PageRequest.of(0, 10)).getSlice();

        @Test
        @DisplayName("[성공] 지역별 모임을 검색할 수 있다.")
        void searchMeetings() {
            // given
            String type = "region";
            String term = "강남구";
            given(meetingRepository.searchMeetings(type, term,
                    PageRequest.of(pageRequest.page(), CustomPageRequest.PAGE_SIZE)))
                    .willReturn(response);

            // when
            Slice<MeetingSearchResponse> response = meetingService.searchMeetings(type, term,
                    pageRequest);

            // then
            assertThat(response.getContent().size()).isEqualTo(1);
            assertThat(response.getContent().get(0)).isEqualTo(meetingSearchResponse());
        }

        @Test
        @DisplayName("[성공] 카테고리별 모임을 검색할 수 있다.")
        void searchMeetingsWithCategory() {
            // given
            String type = "category";
            String term = "한식";
            given(meetingRepository.searchMeetings(type, term,
                    PageRequest.of(pageRequest.page(), CustomPageRequest.PAGE_SIZE)))
                    .willReturn(response);

            // when
            Slice<MeetingSearchResponse> response = meetingService.searchMeetings(type, term,
                    pageRequest);

            // then
            assertThat(response.getContent().size()).isEqualTo(1);
            assertThat(response.getContent().get(0)).isEqualTo(meetingSearchResponse());
        }

        @Test
        @DisplayName("[성공] 식당 이름으로 모임을 검색할 수 있다.")
        void searchMeetingsWithRestaurantName() {
            // given
            String type = "restaurantName";
            String term = "식당 이름";
            given(meetingRepository.searchMeetings(type, term,
                    PageRequest.of(pageRequest.page(), CustomPageRequest.PAGE_SIZE)))
                    .willReturn(response);

            // when
            Slice<MeetingSearchResponse> response = meetingService.searchMeetings(type, term,
                    pageRequest);

            // then
            assertThat(response.getContent().size()).isEqualTo(1);
            assertThat(response.getContent().get(0)).isEqualTo(meetingSearchResponse());
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