package com.leteatgo.global.circuitbreaker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.leteatgo.domain.tastyrestaurant.dto.request.SearchRestaurantsRequest;
import com.leteatgo.domain.tastyrestaurant.service.TastyRestaurantService;
import com.leteatgo.global.external.exception.ApiBadRequestException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreaker.State;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Disabled
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@SpringBootTest
public class CircuitBreakerTest {

    @Autowired
    TastyRestaurantService tastyRestaurantService;

    @Autowired
    CircuitBreakerRegistry circuitBreakerRegistry;

    @Test
    @DisplayName("지정한 횟수만큼 실패하면 서킷이 열린다.")
    void circuitBreaker() {
        // given
        String CB_SEARCH_RESTAURANTS = "searchRestaurants";
        int failCalls = 5;

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(
                CB_SEARCH_RESTAURANTS);

        SearchRestaurantsRequest request = SearchRestaurantsRequest.builder()
                .keyword("test")
                .page(50)
                .build();

        // when
        for (int i = 0; i < failCalls; i++) {
            try {
                tastyRestaurantService.searchRestaurants(request);
            } catch (ApiBadRequestException e) {
            }
        }

        // then
        assertEquals(State.OPEN, circuitBreaker.getState());
    }
}
