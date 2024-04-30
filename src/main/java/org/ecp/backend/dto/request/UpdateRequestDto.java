package org.ecp.backend.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
@NoArgsConstructor
public class UpdateRequestDto {
    private String code;
    private String description;
    private MultipartFile[] images;
}
