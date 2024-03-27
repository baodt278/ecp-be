package org.ecp.backend.controller;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.dto.request.LoginRequest;
import org.ecp.backend.dto.request.RegisterRequest;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.service.AdminService;
import org.ecp.backend.service.ClientService;
import org.ecp.backend.service.EmployeeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final AdminService adminService;
    private final ClientService clientService;
    private final EmployeeService employeeService;

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
}
