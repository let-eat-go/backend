package com.leteatgo.global.validator;


import static com.leteatgo.global.exception.ErrorCode.INTERNAL_ERROR;

import com.leteatgo.global.storage.exception.StorageException;
import com.leteatgo.global.validator.annotation.ValidFile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.util.List;
import org.apache.tika.Tika;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {
    private final Tika tika = new Tika();
    private static final List<String> WHITE_LIST = List.of("image/jpeg", "image/jpg",
            "image/tiff", "image/png", "image/gif", "image/bmp", "image/webp");

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (!ObjectUtils.isEmpty(value)) { // file required false
            if (!StringUtils.hasText(value.getOriginalFilename())) {
                addMessage(context, "Filename does not exist.");
                return false;
            }

            String mimeType = getMimeType(value);
            return WHITE_LIST.stream().anyMatch(o -> o.equalsIgnoreCase(mimeType));
        }

        return true;
    }

    private String getMimeType(MultipartFile value) {
        try {
            return tika.detect(value.getInputStream());
        } catch (IOException e) {
            throw new StorageException(INTERNAL_ERROR, "get mime type failed");
        }
    }

    private void addMessage(ConstraintValidatorContext context, String msg) {
        context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
    }
}
