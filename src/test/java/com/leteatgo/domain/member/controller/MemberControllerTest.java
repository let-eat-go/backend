package com.leteatgo.domain.member.controller;


import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leteatgo.domain.member.dto.response.MyInfoResponse;
import com.leteatgo.domain.member.service.MemberService;
import com.leteatgo.global.security.jwt.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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

        given(memberService.myInformation(authId))
                .willReturn(myInfoResponse);

        // when
        // then
        mockMvc.perform(get(URI + "/me"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("내 정보 조회",
                        resource(ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("내 정보 조회")
                                .build())
                        )
                );
    }
}