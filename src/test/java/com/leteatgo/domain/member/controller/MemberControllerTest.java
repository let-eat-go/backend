package com.leteatgo.domain.member.controller;


import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.leteatgo.global.type.RestaurantCategory.ASIAN_CUISINE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartBody;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leteatgo.domain.member.dto.request.UpdateInfoRequest;
import com.leteatgo.domain.member.dto.response.MemberProfileResponse;
import com.leteatgo.domain.member.dto.response.MyMeetingsResponse;
import com.leteatgo.domain.member.dto.response.MyMeetingsResponse.Restaurant;
import com.leteatgo.domain.member.service.MemberService;
import com.leteatgo.domain.member.type.SearchType;
import com.leteatgo.global.dto.CustomPageRequest;
import com.leteatgo.global.security.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.Cookie;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

@WithMockUser(username = "1", roles = "USER")
@AutoConfigureRestDocs
@WebMvcTest(
        controllers = MemberController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                JwtAuthenticationFilter.class
                        })
        }
)
class MemberControllerTest {

    @MockBean
    MemberService memberService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    static final String URI = "/api/members";
    static final String TAG = "Member";

    @Test
    @DisplayName("내 정보 조회")
    void myInformation() throws Exception {
        // given
        String authId = "1";

        MemberProfileResponse memberProfileResponse = MemberProfileResponse.builder()
                .nickname("nick")
                .profile("profile url")
                .introduce("introduce")
                .mannerTemperature(36.5)
                .build();

        given(memberService.getProfile(Long.parseLong(authId)))
                .willReturn(memberProfileResponse);

        // when
        // then
        mockMvc.perform(get(URI + "/me")
                        .cookie(new Cookie("access_token", "token"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("내 정보 조회",
                        resource(ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("내 정보 조회")
                                .build())
                ));
    }

    @Nested
    @DisplayName("회원 수정")
    class UpdateInfoMethod {

        String authId = "1";
        UpdateInfoRequest request = new UpdateInfoRequest("nick", "introduce");
        MockMultipartFile json;
        MockMultipartFile profile;

        MockMultipartHttpServletRequestBuilder builder =
                multipart(URI);

        @BeforeEach
        void setup() throws IOException {
            json = new MockMultipartFile(
                    "request",
                    "json",
                    "application/json",
                    objectMapper.writeValueAsBytes(request)
            );

            profile = new MockMultipartFile(
                    "profile",
                    "profile.jpeg",
                    "image/jpeg",
                    new FileInputStream("src/test/resources/img/profile.jpeg")
            );

            // restdocs multipart with patch
            builder.with(request -> {
                request.setMethod("PATCH");
                return request;
            });
        }

        @Test
        @DisplayName("성공 - 별명, 소개, 프로필 모두 수정하는 경우")
        void updateInfo() throws Exception {
            // given
            doNothing().when(memberService).updateInfo(request, profile, Long.parseLong(authId));

            // when
            // then
            mockMvc.perform(builder
                            .file(json)
                            .file(profile)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .content(objectMapper.writeValueAsString(request))
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("회원 수정",
                            requestPartBody("request"),
                            requestPartBody("profile"),
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("회원 수정")
                                    .description("[해당 API는 Swagger에서 올바른 테스트가 불가합니다.] "
                                            + "RestDocs + Swagger에서 multipart file 테스트가 제한되어 있습니다. 실행 시 에러가 발생합니다.")
                                    .build())
                    ));
        }

        @Test
        @DisplayName("성공 - 별명, 소개만 수정하는 경우")
        void updateInfo_withProfile() throws Exception {
            // given
            doNothing().when(memberService).updateInfo(request, null, Long.parseLong(authId));

            // when
            // then
            mockMvc.perform(builder
                            .file(json)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .content(objectMapper.writeValueAsString(request))
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("회원 수정",
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("회원 수정")
                                    .build())
                    ));
        }

        @Test
        @DisplayName("실패 - 별명이 빈값인 경우")
        void updateInfo_empty_nickname() throws Exception {
            // given
            UpdateInfoRequest request = new UpdateInfoRequest("", "introduce");
            json = new MockMultipartFile(
                    "request",
                    "json",
                    "application/json",
                    objectMapper.writeValueAsBytes(request)
            );

            // when
            // then
            mockMvc.perform(builder
                            .file(json)
                            .file(profile)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("회원 수정 실패 - 별명이 빈값",
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("회원 수정")
                                    .build())
                    ));
        }

        @Test
        @DisplayName("실패 - 소개가 빈값인 경우")
        void updateInfo_empty_introduce() throws Exception {
            // given
            UpdateInfoRequest request = new UpdateInfoRequest("nick", null);
            json = new MockMultipartFile(
                    "request",
                    "json",
                    "application/json",
                    objectMapper.writeValueAsBytes(request)
            );

            // when
            // then
            mockMvc.perform(builder
                            .file(json)
                            .file(profile)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("회원 수정 실패 - 소개가 빈값",
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("회원 수정")
                                    .build())
                    ));

        }

        @Test
        @DisplayName("실패 - 프로필 파일이 유효하지 않은 경우")
        void updateInfo_invalid_file() throws Exception {
            // given
            profile = new MockMultipartFile(
                    "profile",
                    "invalidFile.json",
                    "application/json",
                    new FileInputStream("src/test/resources/img/invalidFile.json")
            );

            // when
            // then
            mockMvc.perform(builder
                            .file(json)
                            .file(profile)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("회원 수정 실패 - 유효하지 않은 파일",
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("회원 수정")
                                    .build())
                    ));

        }
    }

    @Test
    @DisplayName("회원 삭제")
    void deleteMember() throws Exception {
        // given
        Long memberId = 1L;
        doNothing().when(memberService).deleteMember(memberId);

        // when
        // then
        mockMvc.perform(delete(URI)
                        .cookie(new Cookie("access_token", "token"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("회원 삭제",
                        resource(ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("회원 삭제")
                                .build())
                ));
    }

    @Nested
    @DisplayName("내 모임 목록 조회")
    class MyMeetingsMethod {

        String authId = "1";
        SearchType type = SearchType.CREATED;
        CustomPageRequest request = new CustomPageRequest(1);

        MyMeetingsResponse response = MyMeetingsResponse.builder()
                .meetingId(1L)
                .meetingName("모여라 참깨")
                .category(ASIAN_CUISINE)
                .maxParticipants(3)
                .restaurant(Restaurant.builder()
                        .id(1L)
                        .name("어머니대성집")
                        .address("서울 동대문구 왕산로11길 4")
                        .phoneNumber("02-123-1234")
                        .build())
                .build();

        List<MyMeetingsResponse> contents = List.of(response);
        SliceImpl<MyMeetingsResponse> slice = new SliceImpl<>(contents,
                PageRequest.of(request.page(), CustomPageRequest.PAGE_SIZE), true);

        @Test
        @DisplayName("성공")
        void myMeetings() throws Exception {
            // given
            given(memberService.myMeetings(type, request, Long.parseLong(authId)))
                    .willReturn(slice);

            // when
            // then
            mockMvc.perform(get(URI + "/meetings/me")
                            .param("type", type.name())
                            .param("page", request.page().toString())
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("내 모임 목록 조회",
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("내 모임 목록 조회")
                                    .build())));
        }

        @Test
        @DisplayName("실패 - 잘못된 조회 타입")
        void myMeetings_() throws Exception {
            // given
            String invalidType = "before";
            given(memberService.myMeetings(any(), any(), any()))
                    .willReturn(slice);

            // when
            // then
            mockMvc.perform(get(URI + "/meetings/me")
                            .param("type", invalidType)
                            .param("page", request.page().toString())
                            .cookie(new Cookie("access_token", "token"))
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("내 모임 목록 조회",
                            resource(ResourceSnippetParameters.builder()
                                    .tag(TAG)
                                    .summary("내 모임 목록 조회 실패")
                                    .build())));
        }
    }

    @Test
    @DisplayName("타 회원 조회")
    void memberProfile() throws Exception {
        // given
        Long memberId = 1L;
        MemberProfileResponse memberProfileResponse = MemberProfileResponse.builder()
                .nickname("nick")
                .profile("profile url")
                .introduce("introduce")
                .mannerTemperature(36.5)
                .build();

        given(memberService.getProfile(memberId))
                .willReturn(memberProfileResponse);

        // when
        // then
        mockMvc.perform(get(URI + "/{memberId}", memberId)
                .cookie(new Cookie("access_token", "token"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("타 회원 조회",
                        resource(ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("타 회원 조회")
                                .build())));
    }
}