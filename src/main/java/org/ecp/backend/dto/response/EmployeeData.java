package org.ecp.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.ecp.backend.enums.Role;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeData {
    private String username;
    private Role role;
    private String fullName;
    private String email;
    private String phone;
    private String address;
}
