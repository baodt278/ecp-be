package org.ecp.backend.controller;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.request.DumbDto;
import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.request.RequestDto;
import org.ecp.backend.dto.request.UpdateRequestDto;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.service.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"*"})
@RequestMapping("/api/client")
public class ClientController {
    private final ClientService clientService;
    private final RequestService requestService;
    private final ContractService contractService;
    private final RecordService recordService;
    private final BillService billService;
    private final CompanyService companyService;

    @PostMapping("/update-info")
    public ServerResponseDto updateInfo(@RequestParam String username,
                                        @RequestBody UserInfoDto dto) {
        return clientService.updateInfo(username, dto);
    }

    @PostMapping("/change-password")
    public ServerResponseDto changePassword(@RequestParam String username,
                                            @RequestBody PasswordRequest dto) {
        return clientService.changePassword(username, dto);
    }

    @GetMapping("/companies")
    public ServerResponseDto getAllCompanies() {
        return companyService.getCompanies();
    }

    @PostMapping(value = "/verify-account", consumes = {"multipart/form-data"})
    public ServerResponseDto verifyAccount(@RequestParam String username,
                                           @ModelAttribute DumbDto dto) {
        return requestService.requestVerify(username, dto);
    }

    @GetMapping("/request-verify")
    public ServerResponseDto getRequests() {
        return requestService.getRequestsForAdmin();
    }

    @GetMapping("/requests")
    public ServerResponseDto getRequests(@RequestParam String username) {
        return requestService.getRequestsForClient(username);
    }

    @PostMapping(value = "/create-request", consumes = {"multipart/form-data"})
    public ServerResponseDto createRequest(@RequestParam String username,
                                           @ModelAttribute RequestDto dto) {
        return requestService.createRequest(username, dto);
    }

    @PostMapping(value = "/update-request", consumes = {"multipart/form-data"})
    public ServerResponseDto updateRequest(@RequestParam String username,
                                           @ModelAttribute UpdateRequestDto dto) {
        return requestService.updateRequest(username, dto);
    }

    @PostMapping("/delete-request")
    public ServerResponseDto deleteRequest(@RequestParam String username,
                                           @RequestParam String code) {
        return requestService.deleteRequest(username, code);
    }

    @GetMapping("/contracts")
    public ServerResponseDto getContracts(@RequestParam String username) {
        return contractService.getContractClientOwner(username);
    }

    @GetMapping("/records/7days-before")
    public ServerResponseDto getRecords7DaysBefore(@RequestParam String contractName) {
        return recordService.findConsumeTime7DaysBefore(contractName);
    }

    @GetMapping("/records/6months-before")
    public ServerResponseDto getTotals6MonthsBefore(@RequestParam String contractName) {
        return recordService.findConsume6MonthsBefore(contractName);
    }

    @GetMapping("/records/current-month")
    public ServerResponseDto getRecordsCurrentMonth(@RequestParam String contractName) {
        return recordService.findRecordsCurrentMonth(contractName);
    }

    @GetMapping("/records/predict-current")
    public ServerResponseDto getPredictCurrentMonth(@RequestParam String contractName) {
        return recordService.predictValueCurrentMonth(contractName);
    }

    @GetMapping("/bills")
    public ServerResponseDto getBillsContract(@RequestParam String contractName) {
        return billService.getBillsContract(contractName);
    }

    @GetMapping("/bills/current-month")
    public ServerResponseDto getBillCurrentMonth(@RequestParam String username) {
        return billService.getBillCurrentMonth(username);
    }
}
