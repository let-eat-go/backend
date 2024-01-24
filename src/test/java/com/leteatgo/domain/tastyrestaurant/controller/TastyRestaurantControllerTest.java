package com.leteatgo.domain.tastyrestaurant.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leteatgo.domain.tastyrestaurant.dto.response.SearchRestaurantResponse;
import com.leteatgo.domain.tastyrestaurant.dto.response.SearchRestaurantResponse.Content;
import com.leteatgo.domain.tastyrestaurant.dto.response.SearchRestaurantResponse.Pagination;
import com.leteatgo.domain.tastyrestaurant.service.TastyRestaurantService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs
@WebMvcTest(TastyRestaurantController.class)
class TastyRestaurantControllerTest {

    @MockBean
    TastyRestaurantService tastyRestaurantService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

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
                .willReturn(new SearchRestaurantResponse(contents, pagination));

        // when
        // then
        mockMvc.perform(get("/api/tasty-restaurants/search")
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
                        queryParameters(
                                parameterWithName("keyword").description("키워드"),
                                parameterWithName("page").description("페이지").optional(),
                                parameterWithName("longitude").description("경도(x)").optional(),
                                parameterWithName("latitude").description("위도(y)").optional(),
                                parameterWithName("radius").description("반경").optional(),
                                parameterWithName("sort").description("정렬 방법").optional()
                        )));
    }
}