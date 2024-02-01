package com.leteatgo.domain.region.entity;

import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Region extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    public Region(String name) {
        this.name = name;
    }

}
