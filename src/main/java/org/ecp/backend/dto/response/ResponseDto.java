package org.ecp.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ecp.backend.enums.Role;

@Data
@AllArgsConstructor
public class ResponseDto {
    private String username;
    private Role role;
}
