package com.leteatgo.global.batch.job;

import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
public class UnmatchedMeetingItemWriter implements ItemWriter<Meeting> {

    private final MeetingRepository meetingRepository;

    @Override
    public void write(Chunk<? extends Meeting> chunk) throws Exception {
        chunk.getItems().forEach(meetingRepository::save);
    }
}
