package org.ecp.backend.service;

import org.ecp.backend.dto.request.DumbDto;
import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.request.LoginRequest;
import org.ecp.backend.dto.request.EmployeeRequest;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.response.ServerResponseDto;

public interface EmployeeService {
    ServerResponseDto login(LoginRequest dto);

    ServerResponseDto changePassword(String username, PasswordRequest dto);

    ServerResponseDto updateInfo(String username, UserInfoDto dto);

    ServerResponseDto create(String acronym, String username, EmployeeRequest dto);

    ServerResponseDto getEmployees(String acronym);

    ServerResponseDto getContracts(String acronym);

    ServerResponseDto getInfo(String username);

    ServerResponseDto uploadAvatar(String username, DumbDto dto);

    ServerResponseDto getCompany(String acronym);

    ServerResponseDto deleteEmployee(String username);
}
