package org.ecp.backend.service;

import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.request.LoginRequest;
import org.ecp.backend.dto.request.RegisterRequest;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.response.ServerResponseDto;

public interface ClientService {
    ServerResponseDto login(LoginRequest dto);

    ServerResponseDto register(RegisterRequest dto);

    ServerResponseDto changePassword(PasswordRequest dto, String username);

    ServerResponseDto updateInfo(UserInfoDto dto, String username);
}