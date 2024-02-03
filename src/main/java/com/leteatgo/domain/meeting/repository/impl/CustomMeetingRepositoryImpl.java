package com.leteatgo.domain.meeting.repository.impl;

import static com.leteatgo.domain.chat.entity.QChatRoom.chatRoom;
import static com.leteatgo.domain.meeting.entity.QMeeting.meeting;
import static com.leteatgo.domain.meeting.entity.QMeetingParticipant.meetingParticipant;
import static com.leteatgo.domain.member.entity.QMember.member;
import static com.leteatgo.domain.tastyrestaurant.entity.QTastyRestaurant.tastyRestaurant;
import static com.leteatgo.global.util.QuerydslUtil.meetingDetailProjection;
import static com.leteatgo.global.util.QuerydslUtil.meetingListProjection;
import static com.leteatgo.global.util.QuerydslUtil.meetingSearchProjection;

import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingListResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingSearchResponse;
import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.repository.CustomMeetingRepository;
import com.leteatgo.domain.meeting.type.MeetingStatus;
import com.leteatgo.domain.meeting.type.SearchType;
import com.leteatgo.global.type.RestaurantCategory;
import com.leteatgo.global.util.SliceUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@RequiredArgsConstructor
public class CustomMeetingRepositoryImpl implements CustomMeetingRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Meeting> findMeetingsForCancel(LocalDateTime startDateTime, MeetingStatus status) {
        return queryFactory
                .selectFrom(meeting)
                .where(meeting.startDateTime.loe(startDateTime)
                        .and(meeting.meetingOptions.status.eq(status)))
                .fetch();
    }

    @Override
    public Optional<MeetingDetailResponse> findMeetingDetail(Long meetingId) {

        MeetingDetailResponse meetingDetailResponse = queryFactory.select(meetingDetailProjection())
                .from(meeting)
                .join(meeting.host, member)
                .join(meeting.chatRoom, chatRoom)
                .leftJoin(meeting.tastyRestaurant, tastyRestaurant)
                .leftJoin(meeting.meetingParticipants, meetingParticipant)
                .where(meeting.id.eq(meetingId))
                .fetchOne();

        return Optional.ofNullable(meetingDetailResponse);
    }

    @Override
    public Slice<MeetingListResponse> findMeetingList(
            String category, String region, Pageable pageable
    ) {

        BooleanBuilder condition = new BooleanBuilder();

        if (category != null) {
            condition.and(meeting.restaurantCategory.eq(RestaurantCategory.from(category)));
        }

        if (region != null) {
            condition.and(meeting.region.name.eq(region));
        }

        List<MeetingListResponse> meetingList = queryFactory.select(meetingListProjection())
                .from(meeting)
                .join(meeting.host, member)
                .leftJoin(meeting.tastyRestaurant, tastyRestaurant)
                .where(condition)
                .orderBy(meeting.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceUtil<>(meetingList, pageable).getSlice();
    }

    @Override
    public Slice<MeetingSearchResponse> searchMeetings(
            String type, String term, Pageable pageable) {

        SearchType searchType = SearchType.getSearchTypeIgnoringCase(type);
        BooleanBuilder condition = new BooleanBuilder();
        // TODO: 인덱스 걸기
        condition.and(
                switch (searchType) {
                    case CATEGORY -> meeting.restaurantCategory.eq(RestaurantCategory.from(term));
                    case REGION -> meeting.region.name.eq(term);
                    case RESTAURANTNAME ->
                            tastyRestaurant.name.containsIgnoreCase(term); // LIKE %term%
                });

        List<MeetingSearchResponse> meetingSearchResponses = queryFactory
                .select(meetingSearchProjection())
                .from(meeting)
                .leftJoin(meeting.tastyRestaurant, tastyRestaurant)
                .where(condition)
                .orderBy(meeting.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceUtil<>(meetingSearchResponses, pageable).getSlice();
    }
}
