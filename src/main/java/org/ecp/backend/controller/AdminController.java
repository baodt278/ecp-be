package org.ecp.backend.controller;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.request.ActionDto;
import org.ecp.backend.dto.request.EmployeeRequest;
import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.CompanyDto;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.service.AdminService;
import org.ecp.backend.service.CompanyService;
import org.ecp.backend.service.EmployeeService;
import org.ecp.backend.service.RequestService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = { "*" })
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final EmployeeService employeeService;
    private final CompanyService companyService;
    private final RequestService requestService;

    @GetMapping("")
    public ServerResponseDto getInfo(@RequestParam String username) {
        return adminService.getInfo(username);
    }

    @PostMapping("/update-info")
    public ServerResponseDto updateInfo(@RequestParam String username, @RequestBody UserInfoDto dto) {
        return adminService.updateInfo(username, dto);
    }

    @PostMapping("/change-password")
    public ServerResponseDto changePassword(@RequestParam String username,
                                            @RequestBody PasswordRequest dto) {
        return adminService.changePassword(username, dto);
    }

    @GetMapping("/companies")
    public ServerResponseDto getAllCompanies() {
        return companyService.getCompanies();
    }

    @PostMapping("/create-company")
    public ServerResponseDto createCompany(@RequestBody CompanyDto dto) {
        return companyService.create(dto);
    }

    @GetMapping("/employees")
    public ServerResponseDto getAllEmployees(@RequestParam String acronym) {
        return employeeService.getEmployees(acronym);
    }

    @PostMapping("/create-employee")
    public ServerResponseDto createEmployee(@RequestParam String acronym,
                                            @RequestParam String username,
                                            @RequestBody EmployeeRequest dto) {
        return employeeService.create(acronym, username, dto);
    }

    @GetMapping("/requests")
    public ServerResponseDto getRequests() {
        return requestService.getRequestsForAdmin();
    }

    @PostMapping("/verify-client")
    public ServerResponseDto verifyClient(@RequestParam String username,
                                          @RequestBody ActionDto dto) {
        return requestService.verifyRequest(username, dto);
    }
}
