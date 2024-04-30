package org.ecp.backend.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class ChargeDto {
    private String reason;
    private Double value;
}
