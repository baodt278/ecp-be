package org.ecp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.ecp.backend.constant.CommonConstant;
import org.ecp.backend.dto.request.DumbDto;
import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.request.LoginRequest;
import org.ecp.backend.dto.request.RegisterRequest;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.response.AdminResponse;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.entity.Admin;
import org.ecp.backend.enums.Role;
import org.ecp.backend.exception.ApplicationRuntimeException;
import org.ecp.backend.repository.AdminRepository;
import org.ecp.backend.repository.EmployeeRepository;
import org.ecp.backend.service.AdminService;
import org.ecp.backend.service.MailService;
import org.ecp.backend.service.MinioService;
import org.ecp.backend.utils.GenerateUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final PasswordEncoder encoder;
    private final AdminRepository adminRepo;
    private final MinioService minioService;
    private final EmployeeRepository employeeRepo;
    private final MailService mailService;

    @Override
    public ServerResponseDto login(LoginRequest dto) {
        Admin admin = adminRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Tài khoản không tồn tại!"));
        if (!encoder.matches(dto.getPassword(), admin.getPassword())) {
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Tài khoản hoặc mật khẩu không đúng!");
        }
        AdminResponse responseDto = new AdminResponse(admin.getUsername(), admin.getRole(), minioService.getUrl(admin.getAvatar()));
        return new ServerResponseDto(CommonConstant.SUCCESS, responseDto);
    }

    @Override
    public ServerResponseDto create(RegisterRequest dto) {
        if (adminRepo.existsByUsername(dto.getUsername()))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Tên đăng nhập đã được sử dụng!");
        if (adminRepo.existsByEmail(dto.getEmail()))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Email đã được sử dụng!");
        Admin admin = Admin.builder()
                .username(dto.getUsername())
                .password(encoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .role(Role.ADMIN)
                .build();
        adminRepo.save(admin);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Tạo tài khoản thành công!");
    }

    @Override
    public ServerResponseDto changePassword(String username, PasswordRequest dto) {
        Admin admin = adminRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!"));
        if (!encoder.matches(dto.getOldPassword(), admin.getPassword()))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Mật khẩu không đúng!");
        admin.setPassword(encoder.encode(dto.getNewPassword()));
        adminRepo.save(admin);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Thay đổi mật khẩu thành công!");
    }

    @Override
    public ServerResponseDto getInfo(String username) {
        Admin admin = adminRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!"));
        UserInfoDto infoDto = new UserInfoDto(admin.getEmail(), admin.getPhone(), admin.getAddress(), admin.getFullName(), minioService.getUrl(admin.getAvatar()));
        return new ServerResponseDto(CommonConstant.SUCCESS, infoDto);
    }

    @Override
    public ServerResponseDto updateInfo(String username, UserInfoDto dto) {
        Admin admin = adminRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!"));
        String email = dto.getEmail();
        if (adminRepo.existsByEmail(email) && !admin.getEmail().equals(email))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Email đã được sử dụng!");
        admin.setEmail(StringUtils.defaultIfEmpty(email, admin.getEmail()));
        admin.setPhone(StringUtils.defaultIfEmpty(dto.getPhone(), admin.getPhone()));
        admin.setFullName(StringUtils.defaultIfEmpty(dto.getFullName(), admin.getFullName()));
        admin.setAvatar(StringUtils.defaultIfEmpty(dto.getAvatar(), admin.getAvatar()));
        adminRepo.save(admin);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Thay đổi thông tin thành công!");
    }

    @Override
    public ServerResponseDto uploadAvatar(String username, DumbDto dto) {
        Admin admin = adminRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!"));
        admin.setAvatar(minioService.uploadFile(dto.getFiles()[0]));
        adminRepo.save(admin);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Thay đổi ảnh đại diện thành công!");
    }

    @Override
    public ServerResponseDto getEmployees(String acronym) {
        return new ServerResponseDto(CommonConstant.SUCCESS, employeeRepo.findEmployees(acronym));
    }
}
