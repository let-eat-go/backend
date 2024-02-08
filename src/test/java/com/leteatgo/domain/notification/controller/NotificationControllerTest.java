package com.leteatgo.domain.notification.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leteatgo.domain.meeting.controller.MeetingController;
import com.leteatgo.domain.notification.dto.NotificationDto;
import com.leteatgo.domain.notification.service.NotificationService;
import com.leteatgo.domain.notification.type.NotificationType;
import com.leteatgo.global.dto.CustomPageRequest;
import com.leteatgo.global.security.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@WithMockUser(username = "1", roles = "USER")
@AutoConfigureRestDocs
@WebMvcTest(
        controllers = NotificationController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                JwtAuthenticationFilter.class
                        })
        }
)
@ActiveProfiles("test")
class NotificationControllerTest {

    @MockBean
    NotificationService notificationService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("[성공] SSE 연결")
    public void subscribe() throws Exception {
        // given
        given(notificationService.subscribe("1")).willReturn(new SseEmitter());
        // when
        // then
        mockMvc.perform(get("/api/notification/subscribe")
                        .contentType("text/event-stream")
                        .accept("text/event-stream")
                        .cookie(new Cookie("access_token", "token"))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("SSE 연결",
                        resource(ResourceSnippetParameters.builder()
                                .tag("notification")
                                .summary("SSE 연결")
                                .build()
                        )
                ));
    }

    @Test
    @DisplayName("[성공] 알림 읽음 처리")
    public void readNotification() throws Exception {
        // given
        doNothing().when(notificationService).readNotification(1L, 1L);
        // when
        // then
        mockMvc.perform(patch("/api/notification/{notificationId}", 1)
                        .cookie(new Cookie("access_token", "token"))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("알림 읽음 처리",
                        resource(ResourceSnippetParameters.builder()
                                .tag("notification")
                                .summary("알림 읽음 처리")
                                .pathParameters(
                                        parameterWithName("notificationId")
                                                .description("알림 ID")
                                )
                                .build()
                        )
                ));
    }

    @Test
    @DisplayName("[성공] 알림 목록 조회")
    public void getNotificationList() throws Exception {
        // given
        NotificationDto notificationDto = NotificationDto.builder()
                .message("알림 메시지")
                .type(NotificationType.CANCEL)
                .relatedUrl("https://www.naver.com")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        List<NotificationDto> notificationDtoList = List.of(notificationDto);
        Slice<NotificationDto> response = new SliceImpl<>(notificationDtoList,
                PageRequest.of(0, 10), true);

        given(notificationService.getNotificationList("1", new CustomPageRequest(1)))
                .willReturn(response);
        // when
        // then
        mockMvc.perform(get("/api/notification/list")
                        .cookie(new Cookie("access_token", "token"))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("알림 목록 조회",
                        resource(ResourceSnippetParameters.builder()
                                .tag("notification")
                                .summary("알림 목록 조회")
                                .build()
                        )
                ));
    }
}