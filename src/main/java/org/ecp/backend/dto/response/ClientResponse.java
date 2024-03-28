package org.ecp.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ecp.backend.enums.Role;
@Data
@AllArgsConstructor
public class ClientResponse {
    private String username;
    private Role role;
    private boolean active;
}
