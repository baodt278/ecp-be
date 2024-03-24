package org.ecp.backend.controller;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.request.EmployeeRequest;
import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.response.CompanyResponse;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.service.AdminService;
import org.ecp.backend.service.CompanyService;
import org.ecp.backend.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final EmployeeService employeeService;
    private final CompanyService companyService;

    @GetMapping("/{username}")
    public ServerResponseDto getInfo(@PathVariable String username) {
        return adminService.getInfo(username);
    }

    @PostMapping("/{username}/update-info")
    public ServerResponseDto updateInfo(@PathVariable String username, @RequestBody UserInfoDto dto) {
        return adminService.updateInfo(username, dto);
    }

    @PostMapping("/{username}/change-password")
    public ServerResponseDto changePassword(@PathVariable String username, @RequestBody PasswordRequest dto) {
        return adminService.changePassword(username, dto);
    }

    @GetMapping("/companies")
    public ServerResponseDto getAllCompanies() {
        return companyService.getCompanies();
    }

    @PostMapping("/create-company")
    public ServerResponseDto createCompany(CompanyResponse dto) {
        return companyService.create(dto);
    }

    @GetMapping("/companies/{acronym}/employees")
    public ServerResponseDto getAllEmployees(@PathVariable String acronym) {
        return employeeService.getEmployees(acronym);
    }

    @PostMapping("/companies/{acronym}/create-employee")
    public ServerResponseDto createEmployee(@PathVariable String acronym,
                                            @RequestParam("creator") String creator,
                                            @RequestBody EmployeeRequest dto) {
        return employeeService.create(acronym, creator, dto);
    }
}
