package com.leteatgo.domain.meeting.repository;

import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingListResponse;
import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.type.MeetingStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomMeetingRepository {

    Optional<MeetingDetailResponse> findMeetingDetail(Long meetingId);

    Slice<MeetingListResponse> findMeetingList(String category, String region, Pageable pageable);

    List<Meeting> findMeetingsForCancel(LocalDateTime startDateTime, MeetingStatus status);

    Slice<MeetingListResponse> searchMeetings(String type, String term, Pageable pageable);

    Optional<Meeting> findMeetingFetch(Long meetingId);
}
