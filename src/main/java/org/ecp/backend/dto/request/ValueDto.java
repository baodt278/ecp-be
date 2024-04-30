package org.ecp.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValueDto {
    private double consume;
    private double normal;
    private double low;
    private double high;
}
