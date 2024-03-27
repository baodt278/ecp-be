package org.ecp.backend.service.impl;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.ecp.backend.Constant.CommonConstant;
import org.ecp.backend.exception.ApplicationRuntimeException;
import org.ecp.backend.service.MinioService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {
    private final MinioClient minioClient;
    @Value("${minio.bucket.name}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file) {
        String uuidName = UUID.randomUUID().toString();
        String contentType = getContentType(file);
        String objectName = uuidName + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        try {
            var imageType = objectName.substring(objectName.lastIndexOf(".") + 1);
            BufferedImage originalObject = ImageIO.read(file.getInputStream());
            var width = Math.min(originalObject.getWidth(), 800);
            byte[] bytes;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(originalObject)
                    .width(width)
                    .keepAspectRatio(true)
                    .outputFormat(imageType)
                    .toOutputStream(outputStream);
            bytes = outputStream.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            minioClient.putObject(PutObjectArgs.builder()
                    .stream(new BufferedInputStream(inputStream), bytes.length, -1)
                    .bucket(bucketName)
                    .object(objectName)
                    .contentType(contentType)
                    .build());
            return objectName;
        } catch (Exception e) {
            throw new RuntimeException("Loi khi upload file", e);
        }
    }

    @NotNull
    private static String getContentType(MultipartFile file) {
        String contentType;
        String filename = file.getOriginalFilename();
        if (filename.endsWith(".png")) {
            contentType = "image/png";
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (filename.endsWith(".pdf")) {
            contentType = "application/pdf";
        } else {
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Khong ho tro kieu dinh dang nay");
        }
        return contentType;
    }

    @Override
    public String getUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(1, TimeUnit.DAYS)
                            .build());
        } catch (Exception ignore) {
            return null;
        }
    }
}
