package com.leteatgo.domain.meeting.service;

import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEMBER;

import com.leteatgo.domain.meeting.dto.request.MeetingCreateRequest;
import com.leteatgo.domain.meeting.dto.request.MeetingOptionsRequest;
import com.leteatgo.domain.meeting.dto.response.MeetingCreateResponse;
import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.repository.MeetingRepository;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.exception.MemberException;
import com.leteatgo.domain.member.repository.MemberRepository;
import com.leteatgo.domain.tastyrestaurant.repository.TastyRestaurantRepository;
import com.leteatgo.global.type.RestaurantCategory;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MeetingService {

    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;
    private final TastyRestaurantRepository tastyRestaurantRepository;

    @Transactional
    public MeetingCreateResponse createMeeting(Long memberId, MeetingCreateRequest request) {
        Member host = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        // 음식점 선택안한 경우
        if (Objects.isNull(request.restaurant())) {
            Meeting meeting = Meeting.builder()
                    .host(host)
                    .name(request.name())
                    .restaurantCategory(RestaurantCategory.from(request.category()))
                    .region(request.region())
                    .minParticipants(request.minParticipants())
                    .maxParticipants(request.maxParticipants())
                    .start_date(request.startDate())
                    .start_time(request.startTime())
                    .description(request.description())
                    .meetingOptions(MeetingOptionsRequest.toEntiy(request.options()))
                    .build();
            meetingRepository.save(meeting);
            return new MeetingCreateResponse(meeting.getId());
        }

        // 음식점 선택한 경우
        return null;
    }
}
