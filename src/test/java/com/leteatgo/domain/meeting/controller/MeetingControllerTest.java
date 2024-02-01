package com.leteatgo.domain.meeting.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.leteatgo.global.exception.ErrorCode.ALREADY_CANCELED_MEETING;
import static com.leteatgo.global.exception.ErrorCode.ALREADY_COMPLETED_MEETING;
import static com.leteatgo.global.exception.ErrorCode.CANNOT_CANCEL_MEETING;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEETING;
import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_REGION;
import static com.leteatgo.global.exception.ErrorCode.NOT_MEETING_HOST;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leteatgo.domain.meeting.dto.request.MeetingCreateRequest;
import com.leteatgo.domain.meeting.dto.request.MeetingOptionsRequest;
import com.leteatgo.domain.meeting.dto.request.MeetingUpdateRequest;
import com.leteatgo.domain.meeting.dto.request.TastyRestaurantRequest;
import com.leteatgo.domain.meeting.dto.response.MeetingCreateResponse;
import com.leteatgo.domain.meeting.exception.MeetingException;
import com.leteatgo.domain.meeting.service.MeetingService;
import com.leteatgo.domain.meeting.type.AgePreference;
import com.leteatgo.domain.meeting.type.AlcoholPreference;
import com.leteatgo.domain.meeting.type.GenderPreference;
import com.leteatgo.domain.meeting.type.MeetingPurpose;
import com.leteatgo.domain.region.exception.RegionException;
import com.leteatgo.global.security.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WithMockUser(username = "1", roles = "USER")
@AutoConfigureRestDocs
@WebMvcTest(
        controllers = MeetingController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                JwtAuthenticationFilter.class
                        })
        }
)
class MeetingControllerTest {

    @MockBean
    MeetingService meetingService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Nested
    @DisplayName("모임 생성")
    class CreateMeeting {

        MeetingOptionsRequest options = new MeetingOptionsRequest(GenderPreference.ANY,
                AgePreference.ANY, MeetingPurpose.DRINKING, AlcoholPreference.ANY);
        TastyRestaurantRequest restaurant = new TastyRestaurantRequest("식당 이름", "123123123", "한식",
                "01012341234", "도로명 주소", "지번 주소", 37.123456, 127.123456, "식당 URL");
        MeetingCreateRequest request1 = MeetingCreateRequest.builder()
                .name("모임 제목")
                .category("한식")
                .region("강남구")
                .maxParticipants(4)
                .minParticipants(2)
                .startDate(LocalDate.of(2024, 1, 31))
                .startTime(LocalTime.of(19, 0))
                .description("모임 설명")
                .options(options)
                .restaurant(restaurant)
                .build();

        MeetingCreateRequest request2 = MeetingCreateRequest.builder()
                .name("모임 제목")
                .category("한식")
                .region("강남구")
                .maxParticipants(4)
                .minParticipants(2)
                .startDate(LocalDate.of(2024, 1, 31))
                .startTime(LocalTime.of(19, 0))
                .description("모임 설명")
                .options(options)
                .build();

        String requestBody1 = objectMapper.writeValueAsString(request1);
        String requestBody2 = objectMapper.writeValueAsString(request2);
        MeetingCreateResponse response = new MeetingCreateResponse(1L);

        CreateMeeting() throws JsonProcessingException {
        }

        @Test
        @DisplayName("[성공] 모임 생성 - 식당을 선택한 경우")
        void createMeeting() throws Exception {
            // given
            given(meetingService.createMeeting(1L, request1))
                    .willReturn(response);
            // when
            // then
            mockMvc.perform(post("/api/meetings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody1)
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf())
                    )
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andDo(print())
                    .andDo(document("모임 생성 - 식당을 선택한 경우",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("meeting")
                                    .summary("모임 생성")
                                    .responseHeaders(
                                            ResourceDocumentation.headerWithName("Location")
                                                    .description("생성된 모임의 URI")
                                    )
                                    .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[성공] 모임 생성 - 식당을 선택하지 않은 경우")
        void createMeetingWithoutRestaurant() throws Exception {
            // given
            given(meetingService.createMeeting(1L, request2))
                    .willReturn(response);
            // when
            // then
            mockMvc.perform(post("/api/meetings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody2)
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf())
                    )
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andDo(print())
                    .andDo(document("모임 생성 - 식당을 선택하지 않은 경우",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("meeting")
                                    .summary("모임 생성")
                                    .responseHeaders(
                                            ResourceDocumentation.headerWithName("Location")
                                                    .description("생성된 모임의 URI")
                                    )
                                    .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[실패] 모임 생성 - 지역이 존재하지 않을 때")
        void createMeetingFailWhenMemberNotFound() throws Exception {
            // given
            given(meetingService.createMeeting(1L, request1))
                    .willThrow(new RegionException(NOT_FOUND_REGION));
            // when
            // then
            mockMvc.perform(post("/api/meetings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody1)
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf())
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("지역이 존재하지 않음",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("meeting")
                                    .summary("모임 생성")
                                    .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("모임 수정")
    class UpdateMeeting {

        TastyRestaurantRequest restaurant = new TastyRestaurantRequest("식당 이름", "123123123", "한식",
                "01012341234", "도로명 주소", "지번 주소", 37.123456, 127.123456, "식당 URL");

        MeetingUpdateRequest request1 = new MeetingUpdateRequest(
                LocalDate.of(2024, 1, 31),
                LocalTime.of(19, 0),
                restaurant
        );

        MeetingUpdateRequest request2 = new MeetingUpdateRequest(
                LocalDate.of(2024, 1, 31),
                LocalTime.of(19, 0),
                null
        );
        String requestBody1 = objectMapper.writeValueAsString(request1);
        String requestBody2 = objectMapper.writeValueAsString(request2);

        UpdateMeeting() throws JsonProcessingException {
        }

        @Test
        @DisplayName("[성공] 모임 수정 - 시간,식당을 선택한 경우")
        void updateMeeting() throws Exception {
            // given
            doNothing().when(meetingService).updateMeeting(1L, 1L, request1);
            // when
            // then
            mockMvc.perform(put("/api/meetings/{meetingId}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody1)
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf())
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("모임 수정 - 시간,식당을 선택한 경우",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("meeting")
                                    .summary("모임 수정")
                                    .pathParameters(
                                            ResourceDocumentation.parameterWithName("meetingId")
                                                    .description("모임 ID")
                                    )
                                    .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[성공] 모임 수정 - 시간만 선택한 경우")
        void updateMeetingWithoutRestaurant() throws Exception {
            // given
            doNothing().when(meetingService).updateMeeting(1L, 1L, request2);
            // when
            // then
            mockMvc.perform(put("/api/meetings/{meetingId}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody2)
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf())
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("모임 수정 - 시간만 선택한 경우",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("meeting")
                                    .summary("모임 수정")
                                    .pathParameters(
                                            ResourceDocumentation.parameterWithName("meetingId")
                                                    .description("모임 ID")
                                    )
                                    .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("모임 취소")
    class CancelMeeting {

        @Test
        @DisplayName("[성공] 모임 취소")
        void cancelMeeting() throws Exception {
            // given
            doNothing().when(meetingService).cancelMeeting(1L, 1L);
            // when
            // then
            mockMvc.perform(delete("/api/meetings/{meetingId}/cancel", 1L)
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf())
                    )
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("모임 취소",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("meeting")
                                    .summary("모임 취소")
                                    .pathParameters(
                                            ResourceDocumentation.parameterWithName("meetingId")
                                                    .description("모임 ID")
                                    )
                                    .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[실패] 모임 취소 - 주최자가 아닌 경우")
        void cancelMeetingFailWhenNotHost() throws Exception {
            // given
            doThrow(new MeetingException(NOT_MEETING_HOST))
                    .when(meetingService).cancelMeeting(1L, 1L);
            // when
            // then
            mockMvc.perform(delete("/api/meetings/{meetingId}/cancel", 1L)
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf())
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("주최자가 아님",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("meeting")
                                    .summary("모임 취소")
                                    .pathParameters(
                                            ResourceDocumentation.parameterWithName("meetingId")
                                                    .description("모임 ID")
                                    )
                                    .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[실패] 모임 취소 - 모임이 존재하지 않는 경우")
        void cancelMeetingFailWhenMeetingNotFound() throws Exception {
            // given
            doThrow(new MeetingException(NOT_FOUND_MEETING))
                    .when(meetingService).cancelMeeting(1L, 1L);
            // when
            // then
            mockMvc.perform(delete("/api/meetings/{meetingId}/cancel", 1L)
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf())
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("모임이 존재하지 않음",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("meeting")
                                    .summary("모임 취소")
                                    .pathParameters(
                                            ResourceDocumentation.parameterWithName("meetingId")
                                                    .description("모임 ID")
                                    )
                                    .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[실패] 모임 취소 - 모임이 이미 취소된 경우")
        void cancelMeetingFailWhenMeetingAlreadyCanceled() throws Exception {
            // given
            doThrow(new MeetingException(ALREADY_CANCELED_MEETING))
                    .when(meetingService).cancelMeeting(1L, 1L);
            // when
            // then
            mockMvc.perform(delete("/api/meetings/{meetingId}/cancel", 1L)
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf())
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("모임이 이미 취소됨",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("meeting")
                                    .summary("모임 취소")
                                    .pathParameters(
                                            ResourceDocumentation.parameterWithName("meetingId")
                                                    .description("모임 ID")
                                    )
                                    .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[실패] 모임 취소 - 모임이 이미 완료된 경우")
        void cancelMeetingFailWhenMeetingAlreadyCompleted() throws Exception {
            // given
            doThrow(new MeetingException(ALREADY_COMPLETED_MEETING))
                    .when(meetingService).cancelMeeting(1L, 1L);
            // when
            // then
            mockMvc.perform(delete("/api/meetings/{meetingId}/cancel", 1L)
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf())
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("모임이 이미 완료됨",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("meeting")
                                    .summary("모임 취소")
                                    .pathParameters(
                                            ResourceDocumentation.parameterWithName("meetingId")
                                                    .description("모임 ID")
                                    )
                                    .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[실패] 모임 취소 - 모임 시작 1시간 전까지만 취소 가능")
        void cancelMeetingFailWhenNotBeforeOneHour() throws Exception {
            // given
            doThrow(new MeetingException(CANNOT_CANCEL_MEETING))
                    .when(meetingService).cancelMeeting(1L, 1L);
            // when
            // then
            mockMvc.perform(delete("/api/meetings/{meetingId}/cancel", 1L)
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf())
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("모임 시작 1시간 전까지만 취소 가능",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("meeting")
                                    .summary("모임 취소")
                                    .pathParameters(
                                            ResourceDocumentation.parameterWithName("meetingId")
                                                    .description("모임 ID")
                                    )
                                    .build()
                            )
                    ));
        }
    }
}