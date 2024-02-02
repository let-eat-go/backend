package com.leteatgo.domain.meeting.repository;

import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse;
import java.util.Optional;

public interface CustomMeetingRepository {

    Optional<MeetingDetailResponse> findMeetingDetail(Long meetingId);
}
