package org.ecp.backend.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecp.backend.enums.Role;

@Data
@NoArgsConstructor
public class EmployeeRequest {
    private String username;
    private String password;
    private String email;
    private String role;
}
