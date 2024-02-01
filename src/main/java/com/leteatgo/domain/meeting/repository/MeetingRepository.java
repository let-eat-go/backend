package com.leteatgo.domain.meeting.repository;

import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.type.MeetingStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @Query("""
            SELECT m FROM Meeting m
            WHERE m.startDateTime <= :startDateTime
            AND m.meetingOptions.status = :status
            """)
    List<Meeting> findMeetingsForCancel(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("status") MeetingStatus status
    );

}