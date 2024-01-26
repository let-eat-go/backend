package com.leteatgo.domain.tastyrestaurant.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.leteatgo.domain.tastyrestaurant.entity.TastyRestaurant;
import com.leteatgo.domain.tastyrestaurant.repository.TastyRestaurantRepository;
import com.leteatgo.global.config.QuerydslConfig;
import com.leteatgo.global.type.RestaurantCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
class CustomTastyRestaurantRepositoryImplTest {

    @Autowired
    TastyRestaurantRepository tastyRestaurantRepository;

    @BeforeEach
    void setup() {
        for (int i = 1; i <= 10; i++) {
            tastyRestaurantRepository.save(TastyRestaurant.builder()
                    .kakaoId(1234L + i)
                    .name("삼환소한마리")
                    .category(RestaurantCategory.한식)
                    .phoneNumber("02-545-2429")
                    .roadAddress("도로명")
                    .landAddress("지번")
                    .latitude(127.06283102249932)
                    .longitude(37.514322572335935)
                    .restaurantUrl("http://place.map.kakao.com/8137464")
                    .numberOfUses(i)
                    .build());
        }
    }

    @Test
    @DisplayName("방문한 맛집 조회 noOffset 페이징 테스트 - 첫 조회")
    void visitedRestaurants_slice_no_offset_first() {
        // given
        // when
        Pageable pageable = PageRequest.ofSize(5);
        Slice<TastyRestaurant> tastyRestaurants =
                tastyRestaurantRepository.visitedRestaurants(null, pageable);

        // then
        assertEquals(5, tastyRestaurants.getContent().size());
        assertEquals(10, tastyRestaurants.getContent().get(0).getNumberOfUses());
        assertEquals(6, tastyRestaurants.getContent().get(4).getNumberOfUses());
    }

    @Test
    @DisplayName("방문한 맛집 조회 noOffset 페이징 테스트")
    void visitedRestaurants_slice_no_offset() {
        // given
        // when
        Pageable pageable = PageRequest.ofSize(5);
        Slice<TastyRestaurant> tastyRestaurants =
                tastyRestaurantRepository.visitedRestaurants(6, pageable);

        // then
        assertEquals(5, tastyRestaurants.getContent().size());
        assertEquals(5, tastyRestaurants.getContent().get(0).getNumberOfUses());
        assertEquals(1, tastyRestaurants.getContent().get(4).getNumberOfUses());
    }
}