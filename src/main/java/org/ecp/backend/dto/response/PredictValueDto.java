package org.ecp.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PredictValueDto {
    private double consume;
    private double normal;
    private double low;
    private double high;
    private double cost;
}
