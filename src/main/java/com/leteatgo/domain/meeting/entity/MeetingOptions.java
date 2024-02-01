package com.leteatgo.domain.meeting.entity;

import com.leteatgo.domain.meeting.type.*;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class MeetingOptions {

    @Enumerated(EnumType.STRING)
    private MeetingStatus status;

    @Enumerated(EnumType.STRING)
    private MeetingPurpose purpose;

    @Enumerated(EnumType.STRING)
    private GenderPreference genderPreference;

    @Enumerated(EnumType.STRING)
    private AgePreference agePreference;

    @Enumerated(EnumType.STRING)
    private AlcoholPreference alcoholPreference;

    @Builder
    public MeetingOptions(MeetingStatus status, MeetingPurpose purpose,
            GenderPreference genderPreference,
            AgePreference agePreference, AlcoholPreference alcoholPreference) {
        this.status = status == null ? MeetingStatus.BEFORE : status;
        this.purpose = purpose;
        this.genderPreference = genderPreference;
        this.agePreference = agePreference;
        this.alcoholPreference = alcoholPreference;
    }

    public void cancel() {
        this.status = MeetingStatus.CANCELED;
    }

    public void complete() {
        this.status = MeetingStatus.COMPLETED;
    }
}
