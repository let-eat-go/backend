package com.leteatgo.domain.review.controller;


import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leteatgo.domain.review.dto.request.ReviewRequest;
import com.leteatgo.domain.review.dto.response.ReviewParticipantResponse;
import com.leteatgo.domain.review.service.ReviewService;
import com.leteatgo.global.security.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.Cookie;
import java.util.List;
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
        controllers = ReviewController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                JwtAuthenticationFilter.class
                        })
        }
)
class ReviewControllerTest {

    @MockBean
    ReviewService reviewService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    static final String URI = "/api/review";
    static final String TAG = "Review";

    @Nested
    @DisplayName("평가하기 메서드")
    class ReviewParticipantMethod {

        Long reviewerId = 1L;
        Long revieweeId = 2L;
        ReviewRequest request = ReviewRequest.builder()
                .meetingId(1L)
                .revieweeId(revieweeId)
                .score(-1.0)
                .build();

        @Test
        @DisplayName("성공")
        void reviewParticipant() throws Exception {
            // given
            doNothing().when(reviewService).reviewParticipant(request, reviewerId);

            // when
            // then
            mockMvc.perform(post(URI)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("평가하기",
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("평가하기")
                                    .build())
                    ));
        }

        @Test
        @DisplayName("실패 - 모임 id가 빈값인 경우")
        void reviewParticipant_empty_meetingId() throws Exception {
            // given
            ReviewRequest request = ReviewRequest.builder()
                    .revieweeId(revieweeId)
                    .score(-1.0)
                    .build();

            doNothing().when(reviewService).reviewParticipant(request, reviewerId);

            // when
            // then
            mockMvc.perform(post(URI)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("평가하기 실패 - 모임 id가 빈값인 경우",
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("평가하기")
                                    .build())
                    ));
        }

        @Test
        @DisplayName("실패 - 평가 받는 모임원 id가 빈값인 경우")
        void reviewParticipant_empty_revieweeId() throws Exception {
            // given
            ReviewRequest request = ReviewRequest.builder()
                    .meetingId(1L)
                    .score(-1.0)
                    .build();

            doNothing().when(reviewService).reviewParticipant(request, reviewerId);

            // when
            // then
            mockMvc.perform(post(URI)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("평가하기 실패 - 평가 받는 모임원 id가 빈값인 경우",
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("평가하기")
                                    .build())
                    ));
        }

        @Test
        @DisplayName("실패 - 평가 점수가 빈값인 경우")
        void reviewParticipant_empty_score() throws Exception {
            // given
            ReviewRequest request = ReviewRequest.builder()
                    .meetingId(1L)
                    .revieweeId(revieweeId)
                    .build();

            doNothing().when(reviewService).reviewParticipant(request, reviewerId);

            // when
            // then
            mockMvc.perform(post(URI)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("평가하기 실패 - 평가 점수가 빈값인 경우",
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("평가하기")
                                    .build())
                    ));
        }
    }

    @Test
    @DisplayName("평가할 모임원 조회")
    void getReviewParticipant() throws Exception {
        // given
        ReviewParticipantResponse.ParticipantResponse participant1 = new ReviewParticipantResponse.ParticipantResponse(
                1L, "nickname1", "profileImageUrl1", true);
        ReviewParticipantResponse.ParticipantResponse participant2 = new ReviewParticipantResponse.ParticipantResponse(
                2L, "nickname2", "profileImageUrl2", false);
        List<ReviewParticipantResponse.ParticipantResponse> participants = List.of(participant1,
                participant2);
        ReviewParticipantResponse response = new ReviewParticipantResponse(participants);

        long meetingId = 1L;
        given(reviewService.getReviewParticipant(1L, meetingId))
                .willReturn(response);

        // when
        // then
        mockMvc.perform(get(URI + "/" + "{meetingId}", meetingId)
                        .cookie(new Cookie("access_token", "token"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("평가할 모임원 조회",
                        resource(ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("평가할 모임원 조회")
                                .pathParameters(
                                        parameterWithName("meetingId")
                                                .description("모임 ID")
                                )
                                .build())
                ));
    }
}