package com.leteatgo.domain.review.repository;

import com.leteatgo.domain.review.entity.Review;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    @Query("SELECT r.reviewee.id FROM Review r WHERE r.reviewer.id = :reviewerId AND r.meeting.id = :meetingId")
    Set<Long> findReviewedMemberIds(Long reviewerId, Long meetingId);
}
