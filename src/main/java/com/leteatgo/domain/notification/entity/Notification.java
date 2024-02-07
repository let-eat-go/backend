package com.leteatgo.domain.notification.entity;

import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.notification.type.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", foreignKey = @ForeignKey(name = "fk_notification_receiver"), nullable = false)
    private Member receiver;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "related_url", nullable = false)
    private String relatedUrl;

    @Builder
    public Notification(String content, Member receiver, NotificationType type, String relatedUrl) {
        this.content = content;
        this.receiver = receiver;
        this.type = type;
        this.relatedUrl = relatedUrl;
    }

    public void addReceiver(Member receiver) {
        this.receiver = receiver;
    }
}
