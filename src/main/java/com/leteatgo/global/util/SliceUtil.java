package com.leteatgo.global.util;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@Getter
public class SliceUtil<T> {

    private final Slice<T> slice;

    public SliceUtil(List<T> contents, Pageable pageable) {
        slice = toSlice(contents, pageable);
    }

    private Slice<T> toSlice(List<T> contents, Pageable pageable) {
        boolean hasNext = false;
        if (contents.size() > pageable.getPageSize()) {
            hasNext = true;
            contents.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }
}
