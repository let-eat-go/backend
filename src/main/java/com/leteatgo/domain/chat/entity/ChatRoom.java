package com.leteatgo.domain.chat.entity;

import static com.leteatgo.domain.chat.type.RoomStatus.*;

import com.leteatgo.domain.chat.type.RoomStatus;
import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", foreignKey = @ForeignKey(name = "fk_chatroom_meeting"), nullable = false)
    private Meeting meeting;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    public ChatRoom(RoomStatus status, Meeting meeting) {
        this.status = status;
        this.meeting = meeting;
    }

    public void closeChatRoom() {
        status = CLOSE;
    }
}
