package org.ecp.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServerResponseDto {
    private int code;
    private Object data;
}
