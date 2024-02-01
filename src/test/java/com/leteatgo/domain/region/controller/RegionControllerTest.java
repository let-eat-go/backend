package com.leteatgo.domain.region.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leteatgo.domain.meeting.controller.MeetingController;
import com.leteatgo.domain.meeting.service.MeetingService;
import com.leteatgo.domain.region.dto.response.RegionInfo;
import com.leteatgo.domain.region.dto.response.RegionResponse;
import com.leteatgo.domain.region.entity.Region;
import com.leteatgo.domain.region.service.RegionService;
import com.leteatgo.global.security.jwt.JwtAuthenticationFilter;
import java.util.Arrays;
import java.util.List;
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
        controllers = RegionController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                JwtAuthenticationFilter.class
                        })
        }
)
class RegionControllerTest {

    @MockBean
    RegionService regionService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Test
    void getRegions() throws Exception {
        // given
        List<Region> regions = Arrays.asList(
                new Region("서울"),
                new Region("경기"),
                new Region("인천")
        );
        RegionResponse regionResponse = new RegionResponse(
                regions.stream()
                        .map(region -> new RegionInfo(region.getId(), region.getName()))
                        .toList()
        );
        given(regionService.getRegions()).willReturn(regionResponse);
        // when
        // then
        mockMvc.perform(get("/api/regions"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("지역 목록 조회",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("regions")
                                        .summary("지역 목록 조회")
                                        .build()
                        )
                ));
    }
}