package com.leteatgo.global.batch.job;

import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.type.MeetingStatus;
import org.springframework.batch.item.ItemProcessor;

public class UnmatchedMeetingItemProcessor implements ItemProcessor<Meeting, Meeting> {

    @Override
    public Meeting process(Meeting meeting) {
        if (meeting.getMeetingOptions().getStatus() == MeetingStatus.BEFORE) {
            meeting.cancel();
        }
        return meeting;
    }
}
