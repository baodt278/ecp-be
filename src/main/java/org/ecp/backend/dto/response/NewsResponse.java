package org.ecp.backend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewsResponse {
    private String code;
    private String author;
    private String title;
    private String content;
    private String time;
    private String companyName;
    private String imageUrls;
}
