package org.ecp.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date time;
    private double consume;
    private double normal;
    private double low;
    private double high;
}
