package org.ecp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecp.backend.enums.ContractStatus;
import org.ecp.backend.enums.ContractType;
import org.ecp.backend.enums.Volt;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractDto {
    private String name;
    private String address;
    private Integer houses;
    private Date createdAt;
    private ContractType type;
    private ContractStatus status;
    private Volt volt;
    private String acronymCompany;
    private String usernameClient;
}
