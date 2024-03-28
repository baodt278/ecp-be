package org.ecp.backend.controller;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.request.AcceptRequest;
import org.ecp.backend.dto.request.ActionDto;
import org.ecp.backend.dto.request.EmployeeRequest;
import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.service.CompanyService;
import org.ecp.backend.service.EmployeeService;
import org.ecp.backend.service.RequestService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employee")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final CompanyService companyService;
    private final RequestService requestService;

    @GetMapping("/all")
    public ServerResponseDto getAllEmployees(@RequestParam String acronym) {
        return employeeService.getEmployees(acronym);
    }

    @GetMapping("")
    public ServerResponseDto getInfo(@RequestParam String username) {
        return employeeService.getInfo(username);
    }

    @GetMapping("/company")
    public ServerResponseDto getCompany(@RequestParam String acronym) {
        return companyService.getCompany(acronym);
    }

    @PostMapping("/create-employee")
    public ServerResponseDto createEmployee(@RequestParam String username,
                                            @RequestParam String acronym,
                                            @RequestBody EmployeeRequest dto) {
        return employeeService.create(acronym, username, dto);
    }

    @PostMapping("/update-info")
    public ServerResponseDto updateInfo(@RequestParam String username,
                                        @RequestBody UserInfoDto dto) {
        return employeeService.updateInfo(username, dto);
    }

    @PostMapping("/change-password")
    public ServerResponseDto changePassword(@RequestParam String username,
                                            @RequestBody PasswordRequest dto) {
        return employeeService.changePassword(username, dto);
    }

    @GetMapping("/staff/requests")
    public ServerResponseDto getRequestsForStaff(@RequestParam String acronym) {
        return requestService.getRequestsForStaff(acronym);
    }

    @GetMapping("/manager/requests")
    public ServerResponseDto getRequestsNeedAccept(@RequestParam String acronym) {
        return requestService.getRequestsForManager(acronym);
    }

    @PostMapping("/staff/review-requests")
    public ServerResponseDto reviewRequest(@RequestParam String username,
                                           @RequestBody ActionDto dto) {
        return requestService.review(username, dto);
    }

    @PostMapping("/manager/accept-requests")
    public ServerResponseDto acceptRequest(@RequestParam String username,
                                           @RequestBody AcceptRequest dto){
        return requestService.accept(username, dto);
    }
}
