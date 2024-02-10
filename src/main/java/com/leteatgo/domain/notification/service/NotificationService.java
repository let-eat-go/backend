package com.leteatgo.domain.notification.service;

import static com.leteatgo.global.constants.Notification.NOTIFICATION_QUEUE;
import static com.leteatgo.global.constants.Notification.SUBSCRIBE;
import static com.leteatgo.global.exception.ErrorCode.CANNOT_READ_NOTIFICATION;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEMBER;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_NOTIFICATION;

import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.exception.MemberException;
import com.leteatgo.domain.member.repository.MemberRepository;
import com.leteatgo.domain.notification.dto.NotificationDto;
import com.leteatgo.domain.notification.entity.Notification;
import com.leteatgo.domain.notification.event.NotificationEvent;
import com.leteatgo.domain.notification.exception.NotificationException;
import com.leteatgo.domain.notification.repository.NotificationRepository;
import com.leteatgo.global.dto.CustomPageRequest;
import com.leteatgo.global.rabbitmq.service.RabbitMQService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
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
        sseEmitterService.send(SUBSCRIBE, memberId, sseEmitter);

        String queue = rabbitMQService.createQueue(NOTIFICATION_QUEUE, memberId);
        rabbitMQService.subscribe(queue);

        sseEmitter.onTimeout(sseEmitter::complete);
        sseEmitter.onError((e) -> sseEmitter.complete());
        sseEmitter.onCompletion(() -> {
            sseEmitterService.deleteSseEmitter(memberId);
            rabbitMQService.removeSubscribe(NOTIFICATION_QUEUE, memberId);
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

        rabbitMQService.publish(NOTIFICATION_QUEUE, event.userId(),
                NotificationDto.fromEntity(notification));
    }

    @Transactional
    public void readNotification(Long memberId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException(NOT_FOUND_NOTIFICATION));
        if (!notification.getReceiver().getId().equals(memberId)) {
            throw new NotificationException(CANNOT_READ_NOTIFICATION);
        }
        notification.readNotification();
    }

    @Transactional(readOnly = true)
    public Slice<NotificationDto> getNotificationList(String memberId, CustomPageRequest request) {
        Member member = memberRepository.findById(Long.parseLong(memberId))
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
        return notificationRepository.findAllByReceiverOrderByCreatedAtDesc(
                        member, PageRequest.of(request.page(), CustomPageRequest.PAGE_SIZE))
                .map(NotificationDto::fromEntity);
    }
}
