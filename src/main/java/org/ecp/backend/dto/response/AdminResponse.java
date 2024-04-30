package org.ecp.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ecp.backend.enums.Role;

@Data
@AllArgsConstructor
public class AdminResponse {
    private String username;
    private Role role;
    private String avatar;
}
