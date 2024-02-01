package com.leteatgo.global.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import org.springframework.util.ObjectUtils;

@Getter
public class SliceResponse<T> {

    private final List<T> contents;
    private final Pagination pagination;

    public SliceResponse(Slice<T> slice) {
        this.contents = ObjectUtils.isEmpty(slice.getContent()) ?
                new ArrayList<>() : slice.getContent();

        this.pagination = new Pagination(slice.getPageable().getPageNumber(),
                slice.hasNext());
    }

    public record Pagination(
            Integer currentPage,
            boolean hasMore
    ) {

    }
}
