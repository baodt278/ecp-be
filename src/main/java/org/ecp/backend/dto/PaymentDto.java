package org.ecp.backend.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PaymentDto {
    private String billCode;
    private MultipartFile[] images;
}
