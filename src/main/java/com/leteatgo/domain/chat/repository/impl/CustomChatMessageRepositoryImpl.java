package com.leteatgo.domain.chat.repository.impl;

import static com.leteatgo.domain.chat.entity.QChatMessage.chatMessage;
import static com.leteatgo.domain.member.entity.QMember.member;

import com.leteatgo.domain.chat.entity.ChatMessage;
import com.leteatgo.domain.chat.entity.ChatRoom;
import com.leteatgo.domain.chat.repository.CustomChatMessageRepository;
import com.leteatgo.global.util.SliceUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@RequiredArgsConstructor
public class CustomChatMessageRepositoryImpl implements CustomChatMessageRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<ChatMessage> findByChatRoomFetch(ChatRoom chatRoom, Pageable pageable) {
        List<ChatMessage> contents = queryFactory.selectFrom(chatMessage)
                .join(chatMessage.sender, member).fetchJoin()
                .where(chatMessage.chatRoom.eq(chatRoom))
                .orderBy(chatMessage.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceUtil<>(contents, pageable).getSlice();
    }
}
