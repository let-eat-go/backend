package com.leteatgo.domain.meeting.repository;

import com.leteatgo.domain.meeting.entity.MeetingParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Long>,
        CustomMeetingParticipantRepository {

}
