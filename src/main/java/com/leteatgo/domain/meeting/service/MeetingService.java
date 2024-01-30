package com.leteatgo.domain.meeting.service;

import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEMBER;

import com.leteatgo.domain.meeting.dto.request.MeetingCreateRequest;
import com.leteatgo.domain.meeting.dto.request.TastyRestaurantRequest;
import com.leteatgo.domain.meeting.dto.response.MeetingCreateResponse;
import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.repository.MeetingRepository;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.exception.MemberException;
import com.leteatgo.domain.member.repository.MemberRepository;
import com.leteatgo.domain.tastyrestaurant.entity.TastyRestaurant;
import com.leteatgo.domain.tastyrestaurant.repository.TastyRestaurantRepository;
import java.util.Objects;
import java.util.Optional;
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
        Meeting meeting = MeetingCreateRequest.toEntity(host, request);

        if (Objects.nonNull(request.restaurant())) {
            TastyRestaurant tastyRestaurant = findOrCreateTastyRestaurant(request.restaurant());
            meeting.addTastyRestaurant(tastyRestaurant);
        }

        meetingRepository.save(meeting);
        return new MeetingCreateResponse(meeting.getId());
    }

    private TastyRestaurant findOrCreateTastyRestaurant(TastyRestaurantRequest request) {
        Optional<TastyRestaurant> tastyRestaurant = tastyRestaurantRepository.findByKakaoId(
                Long.parseLong(request.kakaoId()));

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
}
