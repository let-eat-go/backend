package com.leteatgo.domain.review.entity;

import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "score", nullable = false)
    private Double score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", foreignKey = @ForeignKey(name = "fk_reviewer"), nullable = false)
    private Member reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", foreignKey = @ForeignKey(name = "fk_reviewee"), nullable = false)
    private Member reviewee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", foreignKey = @ForeignKey(name = "fk_review_meeting"), nullable = false)
    private Meeting meeting;

    @Builder
    public Review(Double score, Member reviewer, Member reviewee, Meeting meeting) {
        this.score = score;
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.meeting = meeting;
    }
}
