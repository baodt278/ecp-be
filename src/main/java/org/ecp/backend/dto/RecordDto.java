package org.ecp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordDto {
    private Date time;
    private double consume;
    private double normal;
    private double low;
    private double high;
}
