package com.leteatgo.domain.meeting.repository;

import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.type.MeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @Query("""
            SELECT m FROM Meeting m
            WHERE m.startDate > :startDate
            AND m.startTime > :startTime
            AND m.meetingOptions.status = :status
            AND m.minParticipants > SIZE(m.meetingParticipants)
            """)
    List<Meeting> findMeetingsForCancel(
            @Param("startDate") LocalDate startDate,
            @Param("startTime") LocalTime startTime,
            @Param("status") MeetingStatus status);

}