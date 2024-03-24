package org.ecp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    private String email;
    private String phone;
    private String address;
    private String fullName;
    private String avatar;
}
