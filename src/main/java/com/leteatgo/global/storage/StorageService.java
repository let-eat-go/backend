package com.leteatgo.global.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    FileDto uploadFile(MultipartFile file);

    void deleteFile(String filename);
}
