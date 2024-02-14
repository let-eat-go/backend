package com.leteatgo.global.external.searchplace.client.kakao.config;

import static com.leteatgo.global.exception.ErrorCode.INTERNAL_ERROR;
import static com.leteatgo.global.exception.ErrorCode.INVALID_REQUEST;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.leteatgo.global.external.searchplace.client.kakao.client.KakaoApiClient;
import com.leteatgo.global.external.exception.ApiBadRequestException;
import com.leteatgo.global.external.exception.ApiException;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class KakaoApiConfig {

    @Value("${external.kakao.key}")
    private String key;

    private static final String BASE_URL = "https://dapi.kakao.com/v2/local/search";

    @Bean
    public KakaoApiClient kakaoApiClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(AUTHORIZATION, key)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError,
                        (request, response) -> {
                            throw new ApiBadRequestException(INVALID_REQUEST);
                        })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError,
                        ((request, response) -> {
                            String body = new String(response.getBody().readAllBytes(),
                                    StandardCharsets.UTF_8);
                            throw new ApiException(INTERNAL_ERROR, body);
                        }))
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(KakaoApiClient.class);
    }
}
