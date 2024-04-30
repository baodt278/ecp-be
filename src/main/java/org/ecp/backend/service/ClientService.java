package org.ecp.backend.service;

import org.ecp.backend.dto.request.DumbDto;
import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.request.LoginRequest;
import org.ecp.backend.dto.request.RegisterRequest;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.response.ServerResponseDto;

public interface ClientService {
    ServerResponseDto login(LoginRequest dto);

    ServerResponseDto register(RegisterRequest dto);

    ServerResponseDto changePassword(String username, PasswordRequest dto);

    ServerResponseDto updateInfo(String username, UserInfoDto dto);

    ServerResponseDto uploadAvatar(String username, DumbDto dto);

    ServerResponseDto getInfo(String username);

    ServerResponseDto getDetailInfo(String username);
}
