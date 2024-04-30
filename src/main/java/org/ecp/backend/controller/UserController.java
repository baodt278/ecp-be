package org.ecp.backend.controller;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.constant.CommonConstant;
import org.ecp.backend.dto.request.LoginRequest;
import org.ecp.backend.dto.request.RegisterRequest;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.entity.Admin;
import org.ecp.backend.entity.Client;
import org.ecp.backend.entity.Employee;
import org.ecp.backend.exception.ApplicationRuntimeException;
import org.ecp.backend.repository.AdminRepository;
import org.ecp.backend.repository.ClientRepository;
import org.ecp.backend.repository.EmployeeRepository;
import org.ecp.backend.service.AdminService;
import org.ecp.backend.service.ClientService;
import org.ecp.backend.service.EmployeeService;
import org.ecp.backend.service.MailService;
import org.ecp.backend.utils.GenerateUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"*"})
@RequestMapping("/api")
public class UserController {
    private final AdminService adminService;
    private final ClientService clientService;
    private final EmployeeService employeeService;
    private final AdminRepository adminRepo;
    private final EmployeeRepository employeeRepo;
    private final ClientRepository clientRepo;
    private final MailService mailService;
    private final PasswordEncoder encoder;

    @PostMapping("/admin-login")
    public ServerResponseDto loginAdmin(@RequestBody LoginRequest dto) {
        return adminService.login(dto);
    }

    @PostMapping("/admin-create")
    public ServerResponseDto createAdmin(@RequestBody RegisterRequest dto) {
        return adminService.create(dto);
    }

    @PostMapping("/client-login")
    public ServerResponseDto loginClient(@RequestBody LoginRequest dto) {
        return clientService.login(dto);
    }

    @PostMapping("/client-register")
    public ServerResponseDto registerClient(@RequestBody RegisterRequest dto) {
        return clientService.register(dto);
    }

    @PostMapping("/employee-login")
    public ServerResponseDto loginEmployee(@RequestBody LoginRequest dto) {
        return employeeService.login(dto);
    }

    @PostMapping("/forgot-password")
    public ServerResponseDto forgotPassword(@RequestParam String email, @RequestParam String type) {
        return forgotPasswordV(email, type);
    }

    private ServerResponseDto forgotPasswordV(String email, String type) {
        String newPassword = GenerateUtils.generatePassword();
        switch (type) {
            case "admin":
                Admin admin = adminRepo.findByEmail(email)
                        .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Email không tồn tại!"));
                admin.setPassword(encoder.encode(newPassword));
                adminRepo.save(admin);
                break;
            case "employee":
                Employee employee = employeeRepo.findByEmail(email)
                        .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Email không tồn tại!"));
                employee.setPassword(encoder.encode(newPassword));
                employeeRepo.save(employee);
                break;
            case "client":
                Client client = clientRepo.findByEmail(email)
                        .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Email không tồn tại!"));
                client.setPassword(encoder.encode(newPassword));
                clientRepo.save(client);
                break;
        }
        mailService.sendMail("doantuanbao2708@gmail.com", "Hệ thống điện", email, "Quên mật khẩu", "Mật khẩu mới của bạn là: " + newPassword);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Mật khẩu đã được gửi đến email của bạn!");
    }
}
