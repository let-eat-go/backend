package com.leteatgo.domain.notification.service;

import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEMBER;

import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.exception.MemberException;
import com.leteatgo.domain.member.repository.MemberRepository;
import com.leteatgo.domain.notification.dto.NotificationDto;
import com.leteatgo.domain.notification.entity.Notification;
import com.leteatgo.domain.notification.event.NotificationEvent;
import com.leteatgo.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final MemberRepository memberRepository;
    private final SseEmitterService sseEmitterService;
    private final RabbitMQService rabbitMQService;
    private final NotificationRepository notificationRepository;

    public SseEmitter subscribe(String memberId) {
        SseEmitter sseEmitter = sseEmitterService.createSseEmitter(memberId);
        sseEmitterService.send("Dummy Data", memberId, sseEmitter);

        rabbitMQService.subscribe(memberId, sseEmitter);

        sseEmitter.onTimeout(sseEmitter::complete);
        sseEmitter.onError((e) -> sseEmitter.complete());
        sseEmitter.onCompletion(() -> {
            sseEmitterService.deleteSseEmitter(memberId);
            rabbitMQService.removeSubscribe(memberId);
        });

        return sseEmitter;
    }

    @Transactional
    public void sendNotification(NotificationEvent event) {
        Member receiver = memberRepository.findById(Long.parseLong(event.userId()))
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
        Notification notification = Notification.builder()
                .content(event.message())
                .receiver(receiver)
                .type(event.type())
                .relatedUrl(event.relatedUrl())
                .build();
        notification.addReceiver(receiver);
        notificationRepository.save(notification);

        rabbitMQService.publish(event.userId(), NotificationDto.fromEntity(notification));
    }
}
