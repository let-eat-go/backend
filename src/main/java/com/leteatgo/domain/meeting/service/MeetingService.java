package com.leteatgo.domain.meeting.service;

import static com.leteatgo.domain.meeting.type.MeetingStatus.CANCELED;
import static com.leteatgo.domain.meeting.type.MeetingStatus.COMPLETED;
import static com.leteatgo.global.exception.ErrorCode.ALREADY_CANCELED_MEETING;
import static com.leteatgo.global.exception.ErrorCode.ALREADY_COMPLETED_MEETING;
import static com.leteatgo.global.exception.ErrorCode.CANNOT_CANCEL_MEETING;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEETING;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEMBER;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_REGION;
import static com.leteatgo.global.exception.ErrorCode.NOT_MEETING_HOST;

import com.leteatgo.domain.chat.event.ChatRoomEventPublisher;
import com.leteatgo.domain.chat.event.dto.CloseChatRoomEvent;
import com.leteatgo.domain.chat.event.dto.CreateChatRoomEvent;
import com.leteatgo.domain.meeting.dto.request.MeetingCreateRequest;
import com.leteatgo.domain.meeting.dto.request.MeetingUpdateRequest;
import com.leteatgo.domain.meeting.dto.request.TastyRestaurantRequest;
import com.leteatgo.domain.meeting.dto.response.MeetingCreateResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingListResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingSearchResponse;
import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.exception.MeetingException;
import com.leteatgo.domain.meeting.repository.MeetingRepository;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.exception.MemberException;
import com.leteatgo.domain.member.repository.MemberRepository;
import com.leteatgo.domain.region.entity.Region;
import com.leteatgo.domain.region.exception.RegionException;
import com.leteatgo.domain.region.repository.RegionRepository;
import com.leteatgo.domain.tastyrestaurant.entity.TastyRestaurant;
import com.leteatgo.domain.tastyrestaurant.repository.TastyRestaurantRepository;
import com.leteatgo.global.dto.CustomPageRequest;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MeetingService {

    private final MemberRepository memberRepository;
    private final RegionRepository regionRepository;
    private final MeetingRepository meetingRepository;
    private final TastyRestaurantRepository tastyRestaurantRepository;
    private final ChatRoomEventPublisher chatRoomEventPublisher;


    /**
     * [모임 생성] 모임 생성 시 모임 장소(식당)을 선택할 수도 있고, 선택하지 않고 생성할 수도 있음
     * <p>
     * 이미 다른 모임에서 선택한 식당이면 해당 식당의 방문 횟수를 증가시키고, 처음 선택한 식당이면 식당을 생성하고 방문 횟수를 1로 설정
     */
    @Transactional
    public MeetingCreateResponse createMeeting(Long memberId, MeetingCreateRequest request) {
        Member host = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
        Region region = regionRepository.findByName(request.region())
                .orElseThrow(() -> new RegionException(NOT_FOUND_REGION));
        Meeting meeting = MeetingCreateRequest.toEntity(host, region, request);

        if (Objects.nonNull(request.restaurant())) {
            TastyRestaurant tastyRestaurant = findOrCreateTastyRestaurant(request.restaurant());
            meeting.addTastyRestaurant(tastyRestaurant);
        }

        meeting.addMeetingParticipant(host);
        meetingRepository.save(meeting);
        chatRoomEventPublisher.publishCreateChatRoom(new CreateChatRoomEvent(meeting.getId()));
        return new MeetingCreateResponse(meeting.getId());
    }

    private TastyRestaurant findOrCreateTastyRestaurant(TastyRestaurantRequest request) {
        Optional<TastyRestaurant> tastyRestaurant = tastyRestaurantRepository.findByApiId(
                Long.parseLong(request.apiId()));

        if (tastyRestaurant.isPresent()) {
            // TODO: 동시에 여러명이 같은 식당을 등록할 경우 문제가 발생할 수 있음 (근데 이런 경우가 있을까?)
            tastyRestaurant.get().increaseNumberOfUses();
            return tastyRestaurant.get();
        } else {
            TastyRestaurant newTastyRestaurant = TastyRestaurantRequest.toEntity(request);
            tastyRestaurantRepository.save(newTastyRestaurant);
            return newTastyRestaurant;
        }
    }

    /* [모임 수정] 주최자는 날짜 및 시간, 모임 장소(식당)을 수정할 수 있음 */
    @Transactional
    public void updateMeeting(Long memberId, Long meetingId, MeetingUpdateRequest request) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingException(NOT_FOUND_MEETING));
        checkHost(memberId, meeting);

        meeting.update(LocalDateTime.of(request.startDate(), request.startTime()));

        if (Objects.nonNull(request.restaurant())) {
            TastyRestaurant tastyRestaurant = findOrCreateTastyRestaurant(request.restaurant());
            meeting.addTastyRestaurant(tastyRestaurant);
        }

        meetingRepository.save(meeting);
    }

    private void checkHost(Long memberId, Meeting meeting) {
        if (!meeting.getHost().getId().equals(memberId)) {
            throw new MeetingException(NOT_MEETING_HOST);
        }
    }

    /* [모임 취소] 주최자는 모임을 취소할 수 있음 (1시간 전까지만 취소 가능) */
    @Transactional
    public void cancelMeeting(Long memberId, Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingException(NOT_FOUND_MEETING));
        checkHost(memberId, meeting);
        checkCancel(meeting);

        meeting.cancel();
        meetingRepository.save(meeting);
        chatRoomEventPublisher.publishCloseChatRoom(new CloseChatRoomEvent(meeting.getId()));
    }

    private void checkCancel(Meeting meeting) {
        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = meeting.getStartDateTime();

        // 이미 지나간건 취소 가능
        if (nowDateTime.isAfter(startDateTime)) {
            return;
        }

        // 이미 취소된 모임인지 확인
        if (meeting.getMeetingOptions().getStatus() == CANCELED) {
            throw new MeetingException(ALREADY_CANCELED_MEETING);
        }

        // 완료된 모임인지 확인
        if (meeting.getMeetingOptions().getStatus() == COMPLETED) {
            throw new MeetingException(ALREADY_COMPLETED_MEETING);
        }

        // 모임 시작 1시간 전에는 취소할 수 없음
        if (nowDateTime.isBefore(startDateTime)
                && nowDateTime.isAfter(startDateTime.minusHours(1))) {
            throw new MeetingException(CANNOT_CANCEL_MEETING);
        }
    }

    /* [모임 상세 조회] 모임 정보, 주최자 정보, 참가자 정보, 식당 정보, 채팅방 ID를 조회 */
    public MeetingDetailResponse getMeetingDetail(Long meetingId) {
        return meetingRepository.findMeetingDetail(meetingId)
                .orElseThrow(() -> new MeetingException(NOT_FOUND_MEETING));
    }

    /* [모임 목록 조회] 기본 10개씩 페이징 처리, 카테고리, 지역에 따라 조회 */
    public Slice<MeetingListResponse> getMeetingList(
            String category, String region, CustomPageRequest request
    ) {
        return meetingRepository.findMeetingList(
                category, region, PageRequest.of(request.page(), CustomPageRequest.PAGE_SIZE));
    }

    /* [모임 검색] 지역, 카테고리, 식당 이름으로 검색 */
    public Slice<MeetingSearchResponse> searchMeetings(
            String type, String term, CustomPageRequest request
    ) {
        return meetingRepository.searchMeetings(
                type, term, PageRequest.of(request.page(), CustomPageRequest.PAGE_SIZE));
    }
}
