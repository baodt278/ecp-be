package org.ecp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.ecp.backend.constant.CommonConstant;
import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.request.LoginRequest;
import org.ecp.backend.dto.request.EmployeeRequest;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.response.EmployeeResponse;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.entity.Admin;
import org.ecp.backend.entity.Company;
import org.ecp.backend.entity.Employee;
import org.ecp.backend.enums.Role;
import org.ecp.backend.exception.ApplicationRuntimeException;
import org.ecp.backend.repository.AdminRepository;
import org.ecp.backend.repository.CompanyRepository;
import org.ecp.backend.repository.EmployeeRepository;
import org.ecp.backend.service.EmployeeService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final PasswordEncoder encoder;
    private final EmployeeRepository employeeRepo;
    private final AdminRepository adminRepo;
    private final CompanyRepository companyRepo;

    @Override
    public ServerResponseDto login(LoginRequest dto) {
        Employee employee = employeeRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Tài khoản không tồn tại!"));
        if (!encoder.matches(dto.getPassword(), employee.getPassword())) {
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Tài khoản hoặc mật khẩu không đúng!");
        }
        EmployeeResponse responseDto = new EmployeeResponse(employee.getUsername(), employee.getCompany().getAcronym(), employee.getRole());
        return new ServerResponseDto(CommonConstant.SUCCESS, responseDto);
    }

    @Override
    public ServerResponseDto changePassword(String username, PasswordRequest dto) {
        Employee employee = employeeRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!"));
        if (!encoder.matches(dto.getOldPassword(), employee.getPassword()))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Mật khẩu không đúng");
        employee.setPassword(encoder.encode(dto.getNewPassword()));
        employeeRepo.save(employee);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Thay đổi mật khẩu thành công!");
    }

    @Override
    public ServerResponseDto updateInfo(String username, UserInfoDto dto) {
        Employee employee = employeeRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!"));
        String email = dto.getEmail();
        if (StringUtils.isNotBlank(email) || employeeRepo.existsByEmail(email)) {
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Email đã được sử dụng!");
        }
        employee.setEmail(StringUtils.defaultIfBlank(email, employee.getEmail()));
        employee.setPhone(StringUtils.defaultIfBlank(dto.getPhone(), employee.getPhone()));
        employee.setAddress(StringUtils.defaultIfBlank(dto.getAddress(), employee.getAddress()));
        employee.setFullName(StringUtils.defaultIfBlank(dto.getFullName(), employee.getFullName()));
        employee.setAvatar(StringUtils.defaultIfBlank(dto.getAvatar(), employee.getAvatar()));
        employeeRepo.save(employee);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Thay đổi thông tin thành công");
    }

    @Override
    public ServerResponseDto create(String acronym, String creator, EmployeeRequest dto) {
        checkPermitRole(creator);
        String username = dto.getUsername();
        String email = dto.getEmail();
        Role role = dto.getRole();
        if (employeeRepo.existsByUsername(username))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Tên đăng nhập đã tồn tại!");
        if (employeeRepo.existsByEmail(email))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Email đã được sử dụng!");
        Company company = companyRepo.findByAcronym(acronym)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Công ty không tồn tại!"));
        Employee employee = Employee.builder()
                .username(username)
                .password(encoder.encode(dto.getPassword()))
                .email(email)
                .role(role == null ? Role.STAFF : role)
                .company(company)
                .build();
        employeeRepo.save(employee);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Tạo tài khoản thành công!");
    }

    private void checkPermitRole(String username) {
        Admin admin = adminRepo.findByUsername(username).orElse(null);
        Employee employee = employeeRepo.findByUsername(username).orElse(null);
        if (admin == null && employee == null)
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Tài khoản không tồn tại!");
        if (admin == null && employee.getRole().equals(Role.STAFF))
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không đủ quyền hạn cho để thực hiện thao tác này!");
    }

    @Override
    public ServerResponseDto getEmployees(String acronym) {
        if (!companyRepo.existsByAcronym(acronym))
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Công ty không tồn tại!");
        return new ServerResponseDto(CommonConstant.SUCCESS, employeeRepo.findEmployees(acronym));
    }

    @Override
    public ServerResponseDto getInfo(String username) {
        Employee employee = employeeRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!"));
        UserInfoDto infoDto = new UserInfoDto(employee.getEmail(), employee.getPhone(), employee.getAddress(), employee.getFullName(), employee.getAvatar());
        return new ServerResponseDto(CommonConstant.SUCCESS, infoDto);
    }
}
