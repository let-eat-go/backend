package com.leteatgo.domain.meeting.entity;

import com.leteatgo.domain.chat.entity.ChatRoom;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.region.entity.Region;
import com.leteatgo.domain.tastyrestaurant.entity.TastyRestaurant;
import com.leteatgo.global.entity.BaseEntity;
import com.leteatgo.global.type.RestaurantCategory;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(name = "idx_meeting_createdAt", columnList = "created_at")
})
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

    @OneToOne(mappedBy = "meeting", orphanRemoval = true)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", foreignKey = @ForeignKey(name = "FK_meeting_region"))
    private Region region;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL)
    private List<MeetingParticipant> meetingParticipants = new ArrayList<>();

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "restaurant_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private RestaurantCategory restaurantCategory;

    @Column(name = "min_participants", nullable = false)
    private Integer minParticipants;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Column(name = "current_participants", nullable = false)
    private Integer currentParticipants = 1;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "description", nullable = false)
    private String description;

    @Embedded
    private MeetingOptions meetingOptions;

    @Builder
    public Meeting(Member host, TastyRestaurant tastyRestaurant, String name,
            RestaurantCategory restaurantCategory, Region region, Integer minParticipants,
            Integer maxParticipants, LocalDateTime startDateTime, String description,
            MeetingOptions meetingOptions) {
        this.host = host;
        this.tastyRestaurant = tastyRestaurant;
        this.name = name;
        this.restaurantCategory = restaurantCategory;
        this.region = region;
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
        this.startDateTime = startDateTime;
        this.description = description;
        this.meetingOptions = meetingOptions;
    }

    public void addTastyRestaurant(TastyRestaurant tastyRestaurant) {
        this.tastyRestaurant = tastyRestaurant;
    }

    public void addMeetingParticipant(Member member) {
        MeetingParticipant meetingParticipant = MeetingParticipant.builder()
                .meeting(this)
                .member(member)
                .build();

        this.meetingParticipants.add(meetingParticipant);
        this.currentParticipants = this.meetingParticipants.size();
    }

    public void removeMeetingParticipant(MeetingParticipant meetingParticipant) {
        this.meetingParticipants.remove(meetingParticipant);
        this.currentParticipants = this.meetingParticipants.size();
    }

    public void update(LocalDateTime startDateTime) {
        if (Objects.nonNull(startDateTime)) {
            this.startDateTime = startDateTime;
        }
    }

    public void cancel() {
        this.meetingOptions.cancel();
    }

    public void inProgress() {
        this.meetingOptions.inProgress();
    }

    public void complete() {
        this.meetingOptions.complete();
    }

    public void confirm() {
        this.meetingOptions.confirm();
    }
}
