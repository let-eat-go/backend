package com.leteatgo.domain.member.controller;


import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartBody;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leteatgo.domain.member.dto.request.UpdateInfoRequest;
import com.leteatgo.domain.member.dto.response.MyInfoResponse;
import com.leteatgo.domain.member.service.MemberService;
import com.leteatgo.global.security.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.Cookie;
import java.io.FileInputStream;
import java.io.IOException;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
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

        MyInfoResponse myInfoResponse = MyInfoResponse.builder()
                .nickname("nick")
                .profile("profile url")
                .introduce("introduce")
                .mannerTemperature(36.5)
                .build();

        given(memberService.myInformation(Long.parseLong(authId)))
                .willReturn(myInfoResponse);

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
                RestDocumentationRequestBuilders.multipart(URI);

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
}