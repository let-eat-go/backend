package com.leteatgo.global.storage.s3;

import static com.leteatgo.global.exception.ErrorCode.INTERNAL_ERROR;

import com.leteatgo.global.storage.FileDto;
import com.leteatgo.global.storage.StorageService;
import com.leteatgo.global.storage.exception.StorageException;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Exception;
import io.awspring.cloud.s3.S3Template;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Component
public class AwsS3Service implements StorageService {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final S3Template s3Template;

    @Override
    public FileDto uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String filename = UUID.randomUUID() + "." + extension;

        ObjectMetadata objectMetadata = ObjectMetadata.builder()
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        try (InputStream inputStream = file.getInputStream()) {
            return new S3FileDto(s3Template.upload(bucketName, filename, inputStream,
                            objectMetadata).getURL().toString(), filename);
        } catch (IOException e) {
            log.error("IOException is occurred. ", e);
            throw new StorageException(INTERNAL_ERROR, "file upload to S3 failed.");
        }
    }

    @Override
    public void deleteFile(String filename) {
        try {
            s3Template.deleteObject(bucketName, filename);
        } catch (S3Exception e) {
            log.error("S3Exception is occurred. ", e);
            throw new StorageException(INTERNAL_ERROR, e.getMessage());
        }
    }
}
