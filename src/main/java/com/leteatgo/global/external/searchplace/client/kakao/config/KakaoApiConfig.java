package com.leteatgo.global.external.searchplace.client.kakao.config;

import static com.leteatgo.global.exception.ErrorCode.INTERNAL_ERROR;
import static com.leteatgo.global.exception.ErrorCode.INVALID_REQUEST;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.leteatgo.global.external.searchplace.client.kakao.client.KakaoApiClient;
import com.leteatgo.global.external.searchplace.client.kakao.exception.KakaoApiException;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class KakaoApiConfig {

    @Value("${external.kakao.key}")
    private String key;

    private static final String BASE_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";

    @Bean
    public KakaoApiClient kakaoApiClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(UriComponentsBuilder.fromHttpUrl(BASE_URL)
                        .queryParam("category_group_code", "FD6")
                        .queryParam("size", "10")
                        .encode()
                        .build()
                        .toUri().toString())
                .defaultHeader(AUTHORIZATION, key)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError,
                        (request, response) -> {
                            throw new KakaoApiException(INVALID_REQUEST);
                        })
                .defaultStatusHandler(HttpStatusCode::isError,
                        ((request, response) -> {
                            String body = new String(response.getBody().readAllBytes(),
                                    StandardCharsets.UTF_8);
                            throw new KakaoApiException(INTERNAL_ERROR, body);
                        }))
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(KakaoApiClient.class);
    }
}
