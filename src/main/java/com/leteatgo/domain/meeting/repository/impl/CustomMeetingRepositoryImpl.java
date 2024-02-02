package com.leteatgo.domain.meeting.repository.impl;

import static com.leteatgo.domain.chat.entity.QChatRoom.chatRoom;
import static com.leteatgo.domain.meeting.entity.QMeeting.meeting;
import static com.leteatgo.domain.meeting.entity.QMeetingParticipant.meetingParticipant;
import static com.leteatgo.domain.member.entity.QMember.member;
import static com.leteatgo.domain.tastyrestaurant.entity.QTastyRestaurant.tastyRestaurant;
import static com.leteatgo.global.util.QuerydslUtil.meetingDetailProjection;
import static com.leteatgo.global.util.QuerydslUtil.meetingListProjection;

import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingListResponse;
import com.leteatgo.domain.meeting.repository.CustomMeetingRepository;
import com.leteatgo.global.type.RestaurantCategory;
import com.leteatgo.global.util.SliceUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@RequiredArgsConstructor
public class CustomMeetingRepositoryImpl implements CustomMeetingRepository {

    private final JPAQueryFactory queryFactory;


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

        BooleanBuilder predicate = new BooleanBuilder();

        if (category != null) {
            predicate.and(tastyRestaurant.category.eq(RestaurantCategory.valueOf(category)));
        }

        if (region != null) {
            predicate.and(meeting.region.name.eq(region));
        }

        List<MeetingListResponse> meetingList = queryFactory.select(meetingListProjection())
                .from(meeting)
                .join(meeting.host, member)
                .leftJoin(meeting.tastyRestaurant, tastyRestaurant)
                .where(predicate)
                .orderBy(meeting.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceUtil<>(meetingList, pageable).getSlice();
    }

}
