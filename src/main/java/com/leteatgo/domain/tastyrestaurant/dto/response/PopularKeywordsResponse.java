package com.leteatgo.domain.tastyrestaurant.dto.response;

import java.util.List;
import java.util.Objects;
import org.springframework.data.redis.core.ZSetOperations;

public record PopularKeywordsResponse(List<Keywords> contents) {

    public record Keywords(
            String keyword,
            int score
    ) {

        public static Keywords from(ZSetOperations.TypedTuple<Object> tuple) {
            return new Keywords(
                    Objects.requireNonNull(tuple.getValue()).toString(),
                    Objects.requireNonNull(tuple.getScore()).intValue()
            );
        }
    }
}
