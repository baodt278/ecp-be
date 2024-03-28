package org.ecp.backend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecp.backend.enums.ContractStatus;
import org.ecp.backend.enums.ContractType;
import org.ecp.backend.enums.Volt;
@Data
@NoArgsConstructor
public class ContractResponse {
    private String name;
    private String address;
    private Integer houses;
    private ContractType type;
    private ContractStatus status;
    private Volt volt;
    private String companyAcronym;
    private String usernameClient;
}
