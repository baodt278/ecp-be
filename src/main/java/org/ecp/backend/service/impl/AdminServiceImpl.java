package org.ecp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.ecp.backend.constant.CommonConstant;
import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.request.LoginRequest;
import org.ecp.backend.dto.request.RegisterRequest;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.response.ResponseDto;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.entity.Admin;
import org.ecp.backend.enums.Role;
import org.ecp.backend.exception.ApplicationRuntimeException;
import org.ecp.backend.repository.AdminRepository;
import org.ecp.backend.service.AdminService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final PasswordEncoder encoder;
    private final AdminRepository adminRepo;

    @Override
    public ServerResponseDto login(LoginRequest dto) {
        Admin admin = adminRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Tai khoan khong ton tai"));
        if (!encoder.matches(dto.getPassword(), admin.getPassword())) {
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Sai tai khoan hoac mat khau");
        }
        ResponseDto responseDto = new ResponseDto(admin.getUsername(), admin.getRole());
        return new ServerResponseDto(CommonConstant.SUCCESS, responseDto);
    }

    @Override
    public ServerResponseDto create(RegisterRequest dto) {
        if (adminRepo.existsByUsername(dto.getUsername()))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Username da ton tai");
        if (adminRepo.existsByEmail(dto.getEmail()))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Email da duoc su dung");
        Admin admin = Admin.builder()
                .username(dto.getUsername())
                .password(encoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .role(Role.ADMIN)
                .build();
        adminRepo.save(admin);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Tao tai khoan thanh cong");
    }

    @Override
    public ServerResponseDto changePassword(String username, PasswordRequest dto) {
        Admin admin = adminRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong tim thay tai khoan"));
        if (!encoder.matches(dto.getOldPassword(), admin.getPassword()))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Sai mat khau");
        admin.setPassword(encoder.encode(dto.getNewPassword()));
        adminRepo.save(admin);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Thay doi mat khau thanh cong");
    }

    @Override
    public ServerResponseDto getInfo(String username) {
        Admin admin = adminRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong tim thay tai khoan"));
        UserInfoDto infoDto = new UserInfoDto(admin.getEmail(), admin.getPhone(), admin.getAddress(), admin.getFullName(), admin.getAvatar());
        return new ServerResponseDto(CommonConstant.SUCCESS, infoDto);
    }

    @Override
    public ServerResponseDto updateInfo(String username, UserInfoDto dto) {
        Admin admin = adminRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong tim thay tai khoan"));
        String email = dto.getEmail();
        if (StringUtils.isNotBlank(email) || adminRepo.existsByEmail(email))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Email da duoc su dung");
        admin.setEmail(StringUtils.defaultIfEmpty(email, admin.getEmail()));
        admin.setPhone(StringUtils.defaultIfEmpty(dto.getPhone(), admin.getPhone()));
        admin.setAddress(StringUtils.defaultIfEmpty(dto.getAddress(), admin.getAddress()));
        admin.setFullName(StringUtils.defaultIfEmpty(dto.getFullName(), admin.getFullName()));
        admin.setAvatar(StringUtils.defaultIfEmpty(dto.getAvatar(), admin.getAvatar()));
        adminRepo.save(admin);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Thay doi thong tin thanh cong");
    }
}
