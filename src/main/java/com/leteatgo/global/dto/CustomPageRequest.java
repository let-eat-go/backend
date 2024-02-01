package com.leteatgo.global.dto;

import org.springframework.util.ObjectUtils;

public record CustomPageRequest(
        Integer page
) {

    public static final int PAGE_SIZE = 10;

    public CustomPageRequest {
        if (ObjectUtils.isEmpty(page)) {
            page = 0;
        } else {
            page -= 1; // request page start 1
        }
    }
}
