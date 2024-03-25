package org.ecp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class DetailDto {
    private String personId;
    private String personNam;
    private Date dateOfBirth;
    private boolean active;
}
