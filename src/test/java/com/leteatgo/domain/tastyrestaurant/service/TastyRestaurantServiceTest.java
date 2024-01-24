package com.leteatgo.domain.tastyrestaurant.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.leteatgo.domain.tastyrestaurant.dto.request.SearchRestaurantsRequest;
import com.leteatgo.domain.tastyrestaurant.dto.response.SearchRestaurantsResponse;
import com.leteatgo.domain.tastyrestaurant.repository.TastyRestaurantRepository;
import com.leteatgo.global.external.searchplace.client.SearchRestaurantClient;
import com.leteatgo.global.external.searchplace.client.kakao.dto.KakaoRestaurantsResponse;
import com.leteatgo.global.external.searchplace.client.kakao.dto.KakaoRestaurantsResponse.Document;
import com.leteatgo.global.external.searchplace.client.kakao.dto.KakaoRestaurantsResponse.Meta;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TastyRestaurantServiceTest {

    @Mock
    SearchRestaurantClient searchRestaurantClient;

    @Mock
    TastyRestaurantRepository tastyRestaurantRepository;

    @InjectMocks
    TastyRestaurantService tastyRestaurantService;

    @Nested
    @DisplayName("맛집 검색 메서드")
    class SearchRestaurantsMethod {

        List<Document> documents = List.of(Document.builder()
                .kakaoId(1234L)
                .name("삼환소한마리")
                .category("음식점 > 한식 > 육류,고기")
                .phoneNumber("02-545-2429")
                .roadAddress("도로명")
                .landAddress("지번")
                .latitude(127.06283102249932)
                .longitude(37.514322572335935)
                .restaurantUrl("http://place.map.kakao.com/8137464")
                .build());

        Meta meta = new Meta(true, 1234);

        @Test
        @DisplayName("성공 - 키워드 검색")
        void searchRestaurants() {
            // given
            given(searchRestaurantClient.searchPlace(any(), any()))
                    .willReturn(new KakaoRestaurantsResponse(documents, meta));

            // when
            SearchRestaurantsRequest request = SearchRestaurantsRequest.builder()
                    .keyword("감자탕")
                    .build();

            SearchRestaurantsResponse response = tastyRestaurantService.searchRestaurants(
                    request);

            // then
            assertEquals(1, response.pagination().currentPage());
            assertEquals("한식", response.contents().get(0).category());
        }

        @Test
        @DisplayName("성공 - 위치 기반 검색")
        void searchRestaurantsWithDistance() {
            // given
            given(searchRestaurantClient.searchRestaurants(any(), any(),
                    any(), any(), any(), any()))
                    .willReturn(new KakaoRestaurantsResponse(documents, meta));

            // when
            SearchRestaurantsRequest request = SearchRestaurantsRequest.builder()
                    .keyword("감자탕")
                    .latitude(127.06283)
                    .longitude(37.51432)
                    .build();

            SearchRestaurantsResponse response = tastyRestaurantService.searchRestaurants(
                    request);

            // then
            assertEquals(1, response.pagination().currentPage());
            assertEquals("한식", response.contents().get(0).category());
        }
    }
}