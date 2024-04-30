package org.ecp.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ecp.backend.enums.Role;

@Data
@AllArgsConstructor
public class EmployeeResponse {
    private String username;
    private String acronymCompany;
    private Role role;
    private String avatar;
}
