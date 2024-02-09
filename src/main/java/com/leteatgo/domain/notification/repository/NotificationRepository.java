package com.leteatgo.domain.notification.repository;

import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.notification.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Slice<Notification> findAllByReceiverOrderByCreatedAtDesc(Member receiver, Pageable pageable);
}
