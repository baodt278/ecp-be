package org.ecp.backend.controller;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.dto.RecordDto;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.request.AcceptRequest;
import org.ecp.backend.dto.request.ActionDto;
import org.ecp.backend.dto.request.EmployeeRequest;
import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = { "*" })
@RequestMapping("/api/employee")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final CompanyService companyService;
    private final RequestService requestService;
    private final RecordService recordService;
    private final BillService billService;

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

    @PostMapping("/staff/review-requests")
    public ServerResponseDto reviewRequest(@RequestParam String username,
                                           @RequestBody ActionDto dto) {
        return requestService.reviewRequest(username, dto);
    }

    @GetMapping("/manager/requests")
    public ServerResponseDto getRequestsNeedAccept(@RequestParam String acronym) {
        return requestService.getRequestsForManager(acronym);
    }

    @GetMapping("/manager/bills")
    public ServerResponseDto getBillsCompany(@RequestParam String acronym,
                                             @RequestParam Date date) {
        return billService.getBillsCompany(acronym, date);
    }

    @GetMapping("/manager/bills-total")
    public ServerResponseDto totalAnalyst(@RequestParam String acronym,
                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date) {
        return billService.totalAnalyst(acronym, date);
    }

    @PostMapping("/manager/accept-requests")
    public ServerResponseDto acceptRequest(@RequestParam String username,
                                           @RequestBody AcceptRequest dto) {
        return requestService.acceptRequest(username, dto);
    }

    @PostMapping("/staff/bill-create")
    public ServerResponseDto manualCreateBill(@RequestParam String contractName,
                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date) {
        return billService.manualCreate(contractName, date);
    }

    @PostMapping("/staff/bill-pay")
    public ServerResponseDto payBill(@RequestParam String code) {
        return billService.payBill(code);
    }

    @PostMapping("/staff/record-create")
    public ServerResponseDto createRecord(@RequestParam String contractName, @RequestBody RecordDto dto) {
        return recordService.createRecord(contractName, dto);
    }
}
