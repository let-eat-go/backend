package com.leteatgo.domain.meeting.repository;

import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingListResponse;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomMeetingRepository {

    Optional<MeetingDetailResponse> findMeetingDetail(Long meetingId);

    Slice<MeetingListResponse> findMeetingList(String category, String region, Pageable pageable);
}
