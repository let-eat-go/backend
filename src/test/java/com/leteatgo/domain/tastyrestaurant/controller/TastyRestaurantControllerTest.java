package com.leteatgo.domain.tastyrestaurant.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leteatgo.domain.tastyrestaurant.dto.response.PopularKeywordsResponse;
import com.leteatgo.domain.tastyrestaurant.dto.response.PopularKeywordsResponse.Keywords;
import com.leteatgo.domain.tastyrestaurant.dto.response.SearchRestaurantsResponse;
import com.leteatgo.domain.tastyrestaurant.dto.response.SearchRestaurantsResponse.Content;
import com.leteatgo.domain.tastyrestaurant.dto.response.SearchRestaurantsResponse.Pagination;
import com.leteatgo.domain.tastyrestaurant.service.TastyRestaurantService;
import com.leteatgo.global.config.SecurityConfig;
import com.leteatgo.global.security.jwt.JwtAuthenticationFilter;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs
@WebMvcTest(
        controllers = TastyRestaurantController.class,
        excludeAutoConfiguration = {
                UserDetailsServiceAutoConfiguration.class,
                SecurityAutoConfiguration.class
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                SecurityConfig.class, JwtAuthenticationFilter.class
                        })
        }
)
class TastyRestaurantControllerTest {

    @MockBean
    TastyRestaurantService tastyRestaurantService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    static final String URI = "/api/tasty-restaurants";
    static final String TAG = "TastyRestaurants";

    @Test
    @DisplayName("맛집 검색")
    void searchRestaurants() throws Exception {
        // given
        Double longitude = 127.06283102249932;
        Double latitude = 37.514322572335935;

        List<Content> contents = List.of(Content.builder()
                .name("삼환소한마리")
                .category("한식")
                .phoneNumber("02-545-2429")
                .roadAddress("도로명")
                .landAddress("지번")
                .latitude(latitude)
                .longitude(longitude)
                .restaurantUrl("http://place.map.kakao.com/8137464")
                .build());

        Pagination pagination = Pagination.builder()
                .currentPage(1)
                .hasMore(true)
                .totalCount(1234)
                .build();

        given(tastyRestaurantService.searchRestaurants(any()))
                .willReturn(new SearchRestaurantsResponse(contents, pagination));

        // when
        // then
        mockMvc.perform(get(URI + "/search")
                        .param("keyword", "감자탕")
                        .param("page", "1")
                        .param("longitude", String.valueOf(longitude))
                        .param("latitude", String.valueOf(latitude))
                        .param("radius", "1000")
                        .param("sort", "distance"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("맛집 검색",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag(TAG)
                                        .summary("맛집 검색")
                                        .description("맛집을 검색한다.")
                                        .requestSchema(null)
                                        .responseSchema(schema("SearchRestaurantResponse"))
                                        .queryParameters(
                                                parameterWithName("keyword").description("키워드"),
                                                parameterWithName("page").description("페이지")
                                                        .optional(),
                                                parameterWithName("longitude").description("경도(x)")
                                                        .optional(),
                                                parameterWithName("latitude").description("위도(y)")
                                                        .optional(),
                                                parameterWithName("radius").description("반경")
                                                        .optional(),
                                                parameterWithName("sort").description("정렬")
                                                        .optional()
                                        )
                                        .responseFields(
                                                fieldWithPath("contents[].name").description(
                                                        "식당 이름"),
                                                fieldWithPath("contents[].category").description(
                                                        "카테고리"),
                                                fieldWithPath("contents[].phoneNumber").description(
                                                        "전화번호"),
                                                fieldWithPath("contents[].roadAddress").description(
                                                        "도로명 주소"),
                                                fieldWithPath("contents[].landAddress").description(
                                                        "지번 주소"),
                                                fieldWithPath("contents[].latitude").description(
                                                        "경도"),
                                                fieldWithPath("contents[].longitude").description(
                                                        "위도"),
                                                fieldWithPath(
                                                        "contents[].restaurantUrl").description(
                                                        "식당 url"),
                                                fieldWithPath("pagination.currentPage").description(
                                                        "현재 페이지"),
                                                fieldWithPath("pagination.hasMore").description(
                                                        "다음 페이지 여부"),
                                                fieldWithPath("pagination.totalCount").description(
                                                        "전체 컨텐츠 개수")
                                        )
                                        .build()
                        )));
    }

    @Test
    @DisplayName("인기 검색어")
    void popularKeywords() throws Exception {
        // given
        List<Keywords> keywords = List.of(new Keywords("돼지국밥", 10),
                new Keywords("안심", 5));

        given(tastyRestaurantService.getKeywordRanking())
                .willReturn(new PopularKeywordsResponse(keywords));

        // when
        // then
        mockMvc.perform(get(URI + "/popular"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("인기 검색어",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag(TAG)
                                        .summary("인기 검색어 목록 조회")
                                        .responseFields(
                                                fieldWithPath("contents[].keyword").description(
                                                        "키워드"),
                                                fieldWithPath("contents[].score").description(
                                                        "검색한 횟수")
                                        )
                                        .build()
                        )));

        verify(tastyRestaurantService, times(1)).getKeywordRanking();
    }
}