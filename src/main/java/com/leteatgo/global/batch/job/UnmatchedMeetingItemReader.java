package com.leteatgo.global.batch.job;

import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.repository.MeetingRepository;
import com.leteatgo.domain.meeting.type.MeetingStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;

@RequiredArgsConstructor
public class UnmatchedMeetingItemReader implements ItemReader<Meeting> {

    private final MeetingRepository meetingRepository;
    private Iterator<Meeting> meetingIterator;


    @Override
    public Meeting read() {
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();
        if (meetingIterator == null) {
            meetingIterator = meetingRepository.findMeetingsForCancel(
                    nowDate, nowTime, MeetingStatus.BEFORE).iterator();
        }

        if (meetingIterator.hasNext()) {
            return meetingIterator.next();
        } else {
            return null;
        }
    }
}
