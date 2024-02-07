package com.leteatgo.global.converter;

import com.leteatgo.domain.member.type.SearchType;
import org.springframework.core.convert.converter.Converter;

public class SearchTypeConverter implements Converter<String, SearchType> {

    @Override
    public SearchType convert(String source) {
        return SearchType.from(source.toUpperCase());
    }
}
