package org.ecp.backend.service.impl;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
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
public class MinioServiceImpl {
    private final MinioClient minioClient;
    @Value("${minio.bucket.name}")
    private String bucketName;

    public String objectUpload(MultipartFile file) {
        String uuidImage = UUID.randomUUID().toString();
        String object = uuidImage + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        try {
            var imageType = object.substring(object.lastIndexOf(".") + 1);
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            var width = Math.min(originalImage.getWidth(), 640);
            byte[] bytes;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(originalImage)
                    .width(width)
                    .keepAspectRatio(true)
                    .outputFormat(imageType)
                    .toOutputStream(outputStream);
            bytes = outputStream.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            minioClient.putObject(PutObjectArgs.builder()
                    .stream(new BufferedInputStream(inputStream), bytes.length, -1)
                    .bucket(bucketName)
                    .object(object)
                    .contentType("image/jpeg")
                    .build());
            return object;
        } catch (Exception e) {
            throw new RuntimeException("Error while upload image to Minio: ", e);
        }
    }

    public String getObject(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (Exception ignore) {
            return null;
        }

    }
}
