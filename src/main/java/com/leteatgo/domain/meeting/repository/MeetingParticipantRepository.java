package com.leteatgo.domain.meeting.repository;

import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.entity.MeetingParticipant;
import com.leteatgo.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Long>,
        CustomMeetingParticipantRepository {

    Optional<MeetingParticipant> findByMeetingAndMember(Meeting meeting, Member member);
}
