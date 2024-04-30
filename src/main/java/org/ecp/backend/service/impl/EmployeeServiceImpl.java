package org.ecp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.ecp.backend.constant.CommonConstant;
import org.ecp.backend.dto.CompanyDto;
import org.ecp.backend.dto.request.DumbDto;
import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.request.LoginRequest;
import org.ecp.backend.dto.request.EmployeeRequest;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.response.CompanyData;
import org.ecp.backend.dto.response.EmployeeResponse;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.entity.Admin;
import org.ecp.backend.entity.Company;
import org.ecp.backend.entity.Employee;
import org.ecp.backend.enums.Role;
import org.ecp.backend.exception.ApplicationRuntimeException;
import org.ecp.backend.repository.*;
import org.ecp.backend.service.EmployeeService;
import org.ecp.backend.service.MinioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final PasswordEncoder encoder;
    private final EmployeeRepository employeeRepo;
    private final AdminRepository adminRepo;
    private final CompanyRepository companyRepo;
    private final MinioService minioService;
    private final ContractRepository contractRepo;
    private final RequestRepository requestRepo;

    @Override
    public ServerResponseDto login(LoginRequest dto) {
        Employee employee = employeeRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Tài khoản không tồn tại!"));
        if (!encoder.matches(dto.getPassword(), employee.getPassword())) {
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Tài khoản hoặc mật khẩu không đúng!");
        }
        EmployeeResponse responseDto = new EmployeeResponse(employee.getUsername(), employee.getCompany().getAcronym(), employee.getRole(), minioService.getUrl(employee.getAvatar()));
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
        if (employeeRepo.existsByEmail(email) && !employee.getEmail().equals(email)) {
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
        Role role = Role.valueOf(dto.getRole());
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
                .role(role)
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
        return new ServerResponseDto(CommonConstant.SUCCESS, employeeRepo.findEmployeeData(acronym));
    }

    @Override
    public ServerResponseDto getContracts(String acronym) {
        if (!companyRepo.existsByAcronym(acronym))
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Công ty không tồn tại!");
        return new ServerResponseDto(CommonConstant.SUCCESS, contractRepo.findContractCompanySign(acronym));
    }

    @Override
    public ServerResponseDto getInfo(String username) {
        Employee employee = employeeRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!"));
        UserInfoDto infoDto = new UserInfoDto(employee.getEmail(), employee.getPhone(), employee.getAddress(), employee.getFullName(), minioService.getUrl(employee.getAvatar()));
        return new ServerResponseDto(CommonConstant.SUCCESS, infoDto);
    }

    @Override
    public ServerResponseDto uploadAvatar(String username, DumbDto dto) {
        Employee employee = employeeRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!"));
        employee.setAvatar(minioService.uploadFile(dto.getFiles()[0]));
        employeeRepo.save(employee);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Thay đổi ảnh đại diện thành công!");
    }

    @Override
    public ServerResponseDto getCompany(String acronym) {
        if (!companyRepo.existsByAcronym(acronym))
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Công ty không tồn tại!");
        CompanyDto dto = companyRepo.findCompanyByAcronym(acronym);
        int numberOfEmployees = employeeRepo.countEmployees(acronym);
        int numberOfContracts = contractRepo.countContracts(acronym);
        int numberOfClients = contractRepo.countClients(acronym);
        int numberOfRequests = requestRepo.countRequests(acronym);
        CompanyData data = new CompanyData(dto.getName(), dto.getAcronym(), dto.getAddress(), numberOfEmployees, numberOfContracts, numberOfClients, numberOfRequests);
        return new ServerResponseDto(CommonConstant.SUCCESS, data);
    }

    @Override
    public ServerResponseDto deleteEmployee(String username) {
        Employee employee = employeeRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!"));
        if (employee.getRole().equals(Role.MANAGER))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Không thể xóa tài khoản quản lý!");
        employeeRepo.delete(employee);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Xóa tài khoản thành công!");
    }
}
