package org.ecp.backend.service;

import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.request.LoginRequest;
import org.ecp.backend.dto.request.RegisterRequest;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.response.ServerResponseDto;

public interface AdminService {
    ServerResponseDto login(LoginRequest dto);

    ServerResponseDto create(RegisterRequest dto);

    ServerResponseDto changePassword(String username, PasswordRequest dto);

    ServerResponseDto getInfo(String username);

    ServerResponseDto updateInfo(String username, UserInfoDto dto);
}
