package org.ecp.backend.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecp.backend.enums.RequestType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
public class RequestDto {
    private String description;
    private List<MultipartFile> images;
    private RequestType type;
    private String info;
    private String acronymCompany;
}
