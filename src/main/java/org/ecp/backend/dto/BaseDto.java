package org.ecp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseDto {
    private String object;
    private String value;
}
