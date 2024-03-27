package org.ecp.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface MinioService {
    String uploadFile(MultipartFile file);

    String getUrl(String objectName);
}
