package com.leteatgo.domain.tastyrestaurant.service;

import static com.leteatgo.global.type.RestaurantCategory.KOREAN_CUISINE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import com.leteatgo.domain.tastyrestaurant.dto.request.SearchRestaurantsRequest;
import com.leteatgo.domain.tastyrestaurant.dto.request.VisitedRestaurantRequest;
import com.leteatgo.domain.tastyrestaurant.dto.response.PopularKeywordsResponse;
import com.leteatgo.domain.tastyrestaurant.dto.response.PopularKeywordsResponse.Keywords;
import com.leteatgo.domain.tastyrestaurant.dto.response.SearchRestaurantsResponse;
import com.leteatgo.domain.tastyrestaurant.dto.response.VisitedRestaurantResponse;
import com.leteatgo.domain.tastyrestaurant.entity.TastyRestaurant;
import com.leteatgo.domain.tastyrestaurant.repository.TastyRestaurantRepository;
import com.leteatgo.global.external.searchplace.client.RestaurantSearcher;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class TastyRestaurantServiceTest {

    @Mock
    RestaurantSearcher searchRestaurantClient;

    @Mock
    TastyRestaurantRepository tastyRestaurantRepository;

    @Mock
    RedisRankingService redisRankingService;

    @InjectMocks
    TastyRestaurantService tastyRestaurantService;

    @Nested
    @DisplayName("맛집 검색 메서드")
    class SearchRestaurantsMethod {

        String keyword = "감자탕";

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
            given(searchRestaurantClient.searchRestaurants(any(), any(),
                    any(), any(), any(), any()))
                    .willReturn(new KakaoRestaurantsResponse(documents, meta));

            doNothing().when(redisRankingService).saveSearchKeyword(any());

            // when
            SearchRestaurantsRequest request = SearchRestaurantsRequest.builder()
                    .keyword(keyword)
                    .build();

            SearchRestaurantsResponse response = tastyRestaurantService.searchRestaurants(
                    request);

            // then
            assertEquals(1, response.pagination().currentPage());
            assertEquals(KOREAN_CUISINE, response.contents().get(0).category());
        }

        @Test
        @DisplayName("성공 - 위치 기반 검색")
        void searchRestaurantsWithDistance() {
            // given
            given(searchRestaurantClient.searchRestaurants(any(), any(),
                    any(), any(), any(), any()))
                    .willReturn(new KakaoRestaurantsResponse(documents, meta));

            doNothing().when(redisRankingService).saveSearchKeyword(any());

            // when
            SearchRestaurantsRequest request = SearchRestaurantsRequest.builder()
                    .keyword(keyword)
                    .latitude(127.06283)
                    .longitude(37.51432)
                    .build();

            SearchRestaurantsResponse response = tastyRestaurantService.searchRestaurants(
                    request);

            // then
            assertEquals(1, response.pagination().currentPage());
            assertEquals(KOREAN_CUISINE, response.contents().get(0).category());
        }
    }

    @Test
    @DisplayName("인기 검색어 목록 조회")
    void getKeywordRankingTop5() {
        // given
        List<Keywords> keywords = List.of(new Keywords("돼지국밥", 3),
                new Keywords("초밥", 2),
                new Keywords("등심", 1));

        given(redisRankingService.getKeywordRanking())
                .willReturn(new PopularKeywordsResponse(keywords));

        // when
        PopularKeywordsResponse rankingTop5 = tastyRestaurantService.getKeywordRanking();

        // then
        assertEquals(3, rankingTop5.contents().size());
        assertEquals(3, rankingTop5.contents().get(0).score());
    }

    @Test
    @DisplayName("회원들이 방문한 맛집 조회")
    void visitedRestaurants() {
        // given
        List<TastyRestaurant> contents = List.of(TastyRestaurant.builder()
                .name("삼환소한마리")
                .category(KOREAN_CUISINE)
                .phoneNumber("02-545-2429")
                .roadAddress("도로명")
                .landAddress("지번")
                .latitude(127.06283102249932)
                .longitude(37.514322572335935)
                .restaurantUrl("http://place.map.kakao.com/8137464")
                .numberOfUses(100)
                .build());

        given(tastyRestaurantRepository.findAllByOrderByNumberOfUsesDesc(any()))
                .willReturn(new SliceImpl<>(contents, PageRequest.of(0, 5), false));

        // when
        VisitedRestaurantResponse response = tastyRestaurantService.visitedRestaurants(
                new VisitedRestaurantRequest(1));

        // then
        assertEquals(1, response.contents().size());
        assertEquals(1, response.pagination().currentPage());
    }
}