package com.leteatgo.domain.chat.entity;

import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "is_read", nullable = false)
    @ColumnDefault("false")
    private boolean isRead = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatting_room_id", foreignKey = @ForeignKey(name = "fk_chatroom_chatmessage"), nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", foreignKey = @ForeignKey(name = "fk_sender_chatmessage"), nullable = false)
    private Member sender;

    public ChatMessage(String content) {
        this.content = content;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public void setSender(Member sender) {
        this.sender = sender;
    }

    public void setRead() {
        this.isRead = true;
    }
}
