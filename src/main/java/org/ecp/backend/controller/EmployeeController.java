package org.ecp.backend.controller;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.dto.RecordDto;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.request.*;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"*"})
@RequestMapping("/api/employee")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final RequestService requestService;
    private final RecordService recordService;
    private final BillService billService;
    private final NewsService newsService;
    private final ContractService contractService;

    @GetMapping("/company")
    public ServerResponseDto getCompany(@RequestParam String acronym) {
        return employeeService.getCompany(acronym);
    }

    @GetMapping("/employees")
    public ServerResponseDto getAllEmployees(@RequestParam String acronym) {
        return employeeService.getEmployees(acronym);
    }

    @GetMapping("/contracts")
    public ServerResponseDto getContracts(@RequestParam String acronym) {
        return employeeService.getContracts(acronym);
    }

    @GetMapping("/info")
    public ServerResponseDto getInfo(@RequestParam String username) {
        return employeeService.getInfo(username);
    }

    @PostMapping(value = "/upload-avatar", consumes = {"multipart/form-data"})
    public ServerResponseDto uploadAvatar(@RequestParam String username,
                                          @ModelAttribute DumbDto dto) {
        return employeeService.uploadAvatar(username, dto);
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

    @PostMapping("/staff/review-request")
    public ServerResponseDto reviewRequest(@RequestParam String username,
                                           @RequestBody ActionDto dto) {
        return requestService.reviewRequest(username, dto);
    }

    @GetMapping("/manager/requests")
    public ServerResponseDto getRequestsNeedAccept(@RequestParam String acronym) {
        return requestService.getRequestsForManager(acronym);
    }

    @GetMapping("/manager/analyst")
    public ServerResponseDto totalAnalyst(@RequestParam String acronym,
                                          @RequestParam String date) {
        return billService.totalAnalyst(acronym, date);
    }

    @PostMapping("/manager/accept-request")
    public ServerResponseDto acceptRequest(@RequestParam String username,
                                           @RequestBody AcceptRequest dto) {
        return requestService.acceptRequest(username, dto);
    }

    @PostMapping("/staff/bill-create")
    public ServerResponseDto manualCreateBill(@RequestParam String contractName,
                                              @RequestParam String date) {
        return billService.manualCreate(contractName, date);
    }

    @PostMapping("/staff/bill-pay")
    public ServerResponseDto payBill(@RequestParam String contractName, @RequestParam String date) {
        return billService.payBill(contractName, date);
    }

    @PostMapping("/staff/create-record")
    public ServerResponseDto createRecord(@RequestParam String contractName,
                                          @RequestParam String date,
                                          @RequestBody ValueDto dto) {
        return recordService.createRecord(contractName, date, dto);
    }

    @GetMapping("/staff/get-contract")
    public ServerResponseDto createRecord(@RequestParam String contractName) {
        return contractService.getContractInfo(contractName);
    }

    @PostMapping("/delete-employee")
    public ServerResponseDto deleteEmployee(@RequestParam String username) {
        return employeeService.deleteEmployee(username);
    }

    @GetMapping("/get-bill")
    public ServerResponseDto getBillByCode(@RequestParam String username, @RequestParam String acronym) {
        return billService.getBillByCode(username, acronym);
    }

    @GetMapping("/request")
    public ServerResponseDto getRequest(@RequestParam String acronym) {
        return requestService.getRequestForCompany(acronym);
    }

    @GetMapping("/news/global")
    public ServerResponseDto getGlobalNews() {
        return newsService.getSystemNews();
    }

    @GetMapping("/news/local")
    public ServerResponseDto getLocalNews(@RequestParam String acronym) {
        return newsService.getLocalNewsForEmployee(acronym);
    }

    @PostMapping(value = "/news/create-local", consumes = {"multipart/form-data"})
    public ServerResponseDto createLocalNews(@RequestParam String acronym,
                                             @ModelAttribute NewsRequest dto) {
        return newsService.createLocalNews(acronym, dto);
    }

    @PostMapping("/news/delete")
    public ServerResponseDto deleteNews(@RequestParam String code) {
        return newsService.deleteNews(code);
    }
}
