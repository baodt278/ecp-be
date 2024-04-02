package org.ecp.backend.utils;

import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.UUID;

public class ImageUtils {

    public static MultipartFile convertImageFromUrlToMultipartFile(String imageUrl) throws IOException {
        String uuidName = UUID.randomUUID().toString();
        String filename = uuidName + "." + getImageExtensionFromUrl(imageUrl);
        URL url = new URL(imageUrl);
        BufferedImage image = ImageIO.read(url);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        byte[] bytes = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        MultipartFile multipartFile = new MultipartFile() {
            @Override
            public String getName() {
                return filename;
            }

            @Override
            public String getOriginalFilename() {
                return filename;
            }

            @Override
            public String getContentType() {
                return "image/jpg";
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return bytes.length;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return bytes;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return inputStream;
            }

            @Override
            public void transferTo(File file) throws IOException, IllegalStateException {
                new FileOutputStream(file).write(bytes);
            }
        };

        return multipartFile;
    }

    private static String getImageExtensionFromUrl(String imageUrl) {
        String[] parts = imageUrl.split("\\.");
        String extension = parts[parts.length - 1];
        int questionMarkIndex = extension.indexOf('?');
        if (questionMarkIndex != -1) {
            extension = extension.substring(0, questionMarkIndex);
        }
        return extension;
    }
}

