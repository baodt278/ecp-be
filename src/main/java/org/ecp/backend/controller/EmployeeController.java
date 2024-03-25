package org.ecp.backend.controller;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.request.EmployeeRequest;
import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.service.CompanyService;
import org.ecp.backend.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employee")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final CompanyService companyService;

    @GetMapping("")
    public ServerResponseDto getAllEmployees(@RequestParam("acronym") String acronym) {
        return employeeService.getEmployees(acronym);
    }

    @GetMapping("/{username}")
    public ServerResponseDto getInfo(@PathVariable String username) {
        return employeeService.getInfo(username);
    }

    @GetMapping("/company")
    public ServerResponseDto getCompany(@RequestParam("acronym") String acronym) {
        return companyService.getCompany(acronym);
    }

    @PostMapping("/{username}/create-employee")
    public ServerResponseDto createEmployee(@PathVariable String username,
                                            @RequestParam("acronym") String acronym,
                                            @RequestBody EmployeeRequest dto) {
        return employeeService.create(acronym, username, dto);
    }

    @PostMapping("/{username}/update-info")
    public ServerResponseDto updateInfo(@PathVariable String username, @RequestBody UserInfoDto dto) {
        return employeeService.updateInfo(username, dto);
    }

    @PostMapping("/{username}/change-password")
    public ServerResponseDto changePassword(@PathVariable String username, @RequestBody PasswordRequest dto) {
        return employeeService.changePassword(username, dto);
    }
}
