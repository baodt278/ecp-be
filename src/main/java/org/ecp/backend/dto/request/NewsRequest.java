package org.ecp.backend.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class NewsRequest {
    private String title;
    private String content;
    private String author;
    private MultipartFile[] images;
}
