package org.ecp.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class TotalDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/yyyy")
    private Date date;
    private Double total;
}
