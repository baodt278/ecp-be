package org.ecp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompanyDto {
    private String name;
    private String acronym;
    private String address;
}
