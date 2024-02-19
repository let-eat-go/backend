package com.leteatgo.domain.meeting.repository;

import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingListResponse;
import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.type.MeetingStatus;
import com.leteatgo.global.type.RestaurantCategory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomMeetingRepository {

    Optional<MeetingDetailResponse> findMeetingDetail(Long meetingId);

    Slice<MeetingListResponse> findMeetingList(RestaurantCategory category, String region,
            Pageable pageable);

    List<Meeting> findMeetingsForUpdateStatus(LocalDateTime now, MeetingStatus status);

    Slice<MeetingListResponse> searchMeetings(String term, Pageable pageable);

    Optional<Meeting> findMeetingFetch(Long meetingId);

    List<Meeting> findMeetingsForRemind(LocalDateTime localDateTime, LocalDateTime otherDateTime,
            MeetingStatus status);
}
