package com.leteatgo.domain.notification.service;

import static com.leteatgo.global.constants.Notification.NOTIFICATION_QUEUE;
import static com.leteatgo.global.exception.ErrorCode.CANNOT_READ_NOTIFICATION;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_NOTIFICATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.repository.MemberRepository;
import com.leteatgo.domain.member.type.LoginType;
import com.leteatgo.domain.member.type.MemberRole;
import com.leteatgo.domain.notification.dto.NotificationDto;
import com.leteatgo.domain.notification.entity.Notification;
import com.leteatgo.domain.notification.event.NotificationEvent;
import com.leteatgo.domain.notification.exception.NotificationException;
import com.leteatgo.domain.notification.repository.NotificationRepository;
import com.leteatgo.domain.notification.type.NotificationType;
import com.leteatgo.global.dto.CustomPageRequest;
import com.leteatgo.global.rabbitmq.service.RabbitMQService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    SseEmitterService sseEmitterService;
    @Mock
    RabbitMQService rabbitMQService;
    @Mock
    NotificationRepository notificationRepository;
    @InjectMocks
    NotificationService notificationService;

    private Member createTestMember(Long id, String email, String nickname, String password,
            String phoneNumber, LoginType loginType, MemberRole role) {
        Member member = Member.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .phoneNumber(phoneNumber)
                .loginType(loginType)
                .role(role)
                .build();

        ReflectionTestUtils.setField(member, "id", id);

        return member;
    }

    @Test
    @DisplayName("[성공] SSE 구독")
    public void subscribe() {
        // given
        String memberId = "1";
        String queueName = "notification";
        SseEmitter mockEmitter = new SseEmitter();

        given(sseEmitterService.createSseEmitter(memberId)).willReturn(mockEmitter);
        given(rabbitMQService.createQueue(NOTIFICATION_QUEUE, memberId)).willReturn(queueName);

        // when
        SseEmitter sseEmitter = notificationService.subscribe(memberId);

        // then
        assertNotNull(sseEmitter);
        verify(sseEmitterService, times(1)).createSseEmitter(memberId);
        verify(sseEmitterService, times(1)).send(anyString(), eq(memberId), eq(mockEmitter));
        verify(rabbitMQService, times(1)).subscribe(queueName);
    }

    @Test
    @DisplayName("[성공] 알림 발송")
    public void sendNotification() {
        // given
        Member member = createTestMember(1L, "test@naver.com", "testnick", "1!qweqwe",
                "01012345678", LoginType.LOCAL, MemberRole.ROLE_USER);
        NotificationEvent event = new NotificationEvent("1", "test message",
                NotificationType.CANCEL, "test url");
        Notification notification = Notification.builder()
                .content(event.message())
                .receiver(member)
                .type(event.type())
                .relatedUrl(event.relatedUrl())
                .build();
        NotificationDto notificationDto = NotificationDto.fromEntity(notification);

        given(memberRepository.findById(member.getId())).willReturn(java.util.Optional.of(member));
        given(notificationRepository.save(any(Notification.class))).willReturn(notification);

        // when
        notificationService.sendNotification(event);

        // then
        verify(rabbitMQService, times(1)).publish(anyString(), eq(event.userId()),
                eq(notificationDto));
    }

    @Nested
    @DisplayName("readNotification 메소드는")
    class readNotificationMethod {

        Member member = createTestMember(1L, "test@naver.com", "testnick", "1!qweqwe",
                "01012345678", LoginType.LOCAL, MemberRole.ROLE_USER);
        Notification notification = Notification.builder()
                .content("test message")
                .receiver(member)
                .type(NotificationType.CANCEL)
                .relatedUrl("test url")
                .build();

        @Test
        @DisplayName("성공 시 알림을 읽음 처리한다.")
        public void readNotification() {
            // given

            given(notificationRepository.findById(1L)).willReturn(Optional.of(notification));

            // when
            notificationService.readNotification(1L, 1L);

            // then
            assertThat(notification.getIsRead()).isTrue();
        }

        @Test
        @DisplayName("알림이 존재하지 않을 경우 예외를 발생시킨다.")
        public void readNotificationWithNotFoundNotification() {
            // given
            given(notificationRepository.findById(1L)).willReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> notificationService.readNotification(member.getId(), 1L))
                    .isInstanceOf(NotificationException.class)
                    .hasMessageContaining(NOT_FOUND_NOTIFICATION.getErrorMessage());
        }

        @Test
        @DisplayName("알림의 수신자가 다를 경우 예외를 발생시킨다.")
        public void readNotificationWithDifferentReceiver() {
            // given
            given(notificationRepository.findById(1L)).willReturn(Optional.of(notification));

            // when
            // then
            assertThatThrownBy(() -> notificationService.readNotification(2L, 1L))
                    .isInstanceOf(NotificationException.class)
                    .hasMessageContaining(CANNOT_READ_NOTIFICATION.getErrorMessage());
        }
    }

    @Nested
    @DisplayName("getNotificationList 메소드는")
    class getNotificationListMethod {

        Member member = createTestMember(1L, "test@naver.com", "testnick", "1!qweqwe",
                "01012345678", LoginType.LOCAL, MemberRole.ROLE_USER);

        Notification notification = Notification.builder()
                .content("test message")
                .receiver(member)
                .type(NotificationType.CANCEL)
                .relatedUrl("test url")
                .build();
        Slice<Notification> notifications = new SliceImpl<>(List.of(notification));

        @Test
        @DisplayName("알림 목록을 조회한다.")
        public void getNotificationList() {
            // given
            CustomPageRequest request = new CustomPageRequest(1);

            given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
            given(notificationRepository.findAllByReceiverOrderByCreatedAtDesc(
                    member, PageRequest.of(request.page(), CustomPageRequest.PAGE_SIZE)))
                    .willReturn(notifications);

            // when
            Slice<NotificationDto> result = notificationService.getNotificationList(
                    member.getId().toString(), request);

            // then
            assertNotNull(result);
            verify(memberRepository, times(1)).findById(member.getId());
            verify(notificationRepository, times(1)).findAllByReceiverOrderByCreatedAtDesc(
                    eq(member), any(PageRequest.class));
        }

        @Test
        @DisplayName("알림 목록이 없을 경우 빈 목록을 반환한다.")
        public void getNotificationListWithEmptyList() {
            // given
            CustomPageRequest request = new CustomPageRequest(1);

            given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
            given(notificationRepository.findAllByReceiverOrderByCreatedAtDesc(
                    member, PageRequest.of(request.page(), CustomPageRequest.PAGE_SIZE)))
                    .willReturn(new SliceImpl<>(List.of()));

            // when
            Slice<NotificationDto> result = notificationService.getNotificationList(
                    member.getId().toString(), request);

            // then
            assertNotNull(result);
            assertThat(result.getContent()).isEmpty();
            verify(memberRepository, times(1)).findById(member.getId());
            verify(notificationRepository, times(1)).findAllByReceiverOrderByCreatedAtDesc(
                    eq(member), any(PageRequest.class));
        }
    }
}