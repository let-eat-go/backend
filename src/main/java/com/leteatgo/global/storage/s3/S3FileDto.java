package com.leteatgo.global.storage.s3;

import com.leteatgo.global.storage.FileDto;

public record S3FileDto(
        String url,
        String filename
) implements FileDto {

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getFilename() {
        return filename;
    }
}
