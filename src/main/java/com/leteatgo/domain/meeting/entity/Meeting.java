package com.leteatgo.domain.meeting.entity;

import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.tastyRestaurant.entity.TastyRestaurant;
import com.leteatgo.global.entity.BaseEntity;
import com.leteatgo.global.type.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;
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
    @JoinColumn(name = "tasty_restaurant_id", nullable = false)
    private TastyRestaurant tastyRestaurant;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "restaurant_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private RestaurantCategory restaurantCategory;

    @Column(name = "region", nullable = false)
    private String region;

    @Column(name = "min_participants", nullable = false)
    private Integer minParticipants;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Column(name = "start_date", nullable = false)
    private LocalDate start_date;

    @Column(name = "start_time", nullable = false)
    private Time start_time;

    @Column(name = "description", nullable = false)
    private String description;

    @Embedded
    private MeetingOptions meetingOptions;

}