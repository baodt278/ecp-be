package org.ecp.backend.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String renewPassword;
}
