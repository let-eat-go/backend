package com.leteatgo.domain.tastyrestaurant.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.leteatgo.domain.tastyrestaurant.dto.request.SearchRestaurantRequest;
import com.leteatgo.domain.tastyrestaurant.dto.response.SearchRestaurantResponse;
import com.leteatgo.domain.tastyrestaurant.repository.TastyRestaurantRepository;
import com.leteatgo.global.external.searchplace.client.SearchPlaceClient;
import com.leteatgo.global.external.searchplace.client.kakao.dto.KakaoSearchPlaceResponse;
import com.leteatgo.global.external.searchplace.client.kakao.dto.KakaoSearchPlaceResponse.Document;
import com.leteatgo.global.external.searchplace.client.kakao.dto.KakaoSearchPlaceResponse.Meta;
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
    SearchPlaceClient searchPlaceClient;

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
            given(searchPlaceClient.searchPlace(any(), any()))
                    .willReturn(new KakaoSearchPlaceResponse(documents, meta));

            // when
            SearchRestaurantRequest request = SearchRestaurantRequest.builder()
                    .keyword("감자탕")
                    .build();

            SearchRestaurantResponse response = tastyRestaurantService.searchRestaurants(
                    request);

            // then
            assertEquals(1, response.pagination().currentPage());
            assertEquals("한식", response.contents().get(0).category());
        }

        @Test
        @DisplayName("성공 - 위치 기반 검색")
        void searchRestaurantsWithDistance() {
            // given
            given(searchPlaceClient.searchPlaceWithDistance(any(), any(),
                    any(), any(), any(), any()))
                    .willReturn(new KakaoSearchPlaceResponse(documents, meta));

            // when
            SearchRestaurantRequest request = SearchRestaurantRequest.builder()
                    .keyword("감자탕")
                    .latitude(127.06283)
                    .longitude(37.51432)
                    .build();

            SearchRestaurantResponse response = tastyRestaurantService.searchRestaurants(
                    request);

            // then
            assertEquals(1, response.pagination().currentPage());
            assertEquals("한식", response.contents().get(0).category());
        }
    }
}