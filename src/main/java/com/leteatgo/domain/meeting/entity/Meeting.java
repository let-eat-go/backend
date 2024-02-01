package com.leteatgo.domain.meeting.entity;

import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.region.entity.Region;
import com.leteatgo.domain.tastyrestaurant.entity.TastyRestaurant;
import com.leteatgo.global.entity.BaseEntity;
import com.leteatgo.global.type.*;
import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Meeting extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", foreignKey = @ForeignKey(name = "FK_meeting_host"), nullable = false)
    private Member host;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tasty_restaurant_id", foreignKey = @ForeignKey(name = "FK_meeting_tasty_restaurant"))
    private TastyRestaurant tastyRestaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", foreignKey = @ForeignKey(name = "FK_meeting_region"))
    private Region region;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL)
    private List<MeetingParticipant> meetingParticipants = new ArrayList<>();

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "restaurant_category", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private RestaurantCategory restaurantCategory;

    @Column(name = "min_participants", nullable = false)
    private Integer minParticipants;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "description", nullable = false)
    private String description;

    @Embedded
    private MeetingOptions meetingOptions;

    @Builder
    public Meeting(Member host, TastyRestaurant tastyRestaurant, String name,
            RestaurantCategory restaurantCategory, Region region, Integer minParticipants,
            Integer maxParticipants, LocalDate startDate, LocalTime startTime, String description,
            MeetingOptions meetingOptions) {
        this.host = host;
        this.tastyRestaurant = tastyRestaurant;
        this.name = name;
        this.restaurantCategory = restaurantCategory;
        this.region = region;
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
        this.startDate = startDate;
        this.startTime = startTime;
        this.description = description;
        this.meetingOptions = meetingOptions;
    }

    public void addTastyRestaurant(TastyRestaurant tastyRestaurant) {
        this.tastyRestaurant = tastyRestaurant;
    }

    public void addMeetingParticipant(Member host) {
        MeetingParticipant meetingParticipant = MeetingParticipant.builder()
                .meeting(this)
                .member(host)
                .build();

        this.meetingParticipants.add(meetingParticipant);
    }

    public void update(LocalDate startDate, LocalTime startTime) {
        if (Objects.nonNull(startDate)) {
            this.startDate = startDate;
        }

        if (Objects.nonNull(startTime)) {
            this.startTime = startTime;
        }
    }

    public void cancel() {
        this.meetingOptions.cancel();
    }

    public void complete() {
        this.meetingOptions.complete();
    }

}
