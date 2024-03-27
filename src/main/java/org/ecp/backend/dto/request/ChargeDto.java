package org.ecp.backend.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecp.backend.enums.ChargeType;
@Data
@NoArgsConstructor
public class ChargeDto {
    private ChargeType type;
    private String reason;
    private Double value;
}
