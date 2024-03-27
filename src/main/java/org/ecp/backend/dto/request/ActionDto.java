package org.ecp.backend.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecp.backend.enums.RequestStatus;

@Data
@NoArgsConstructor
public class ActionDto {
    private String code;
    private String text;
    private RequestStatus status;
}
