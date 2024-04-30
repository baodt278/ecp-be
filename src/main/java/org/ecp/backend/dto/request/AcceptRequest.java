package org.ecp.backend.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AcceptRequest {
    private String code;
    private String text;
    private String status;
    private List<ChargeDto> charges;
}
