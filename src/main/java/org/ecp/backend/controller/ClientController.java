package org.ecp.backend.controller;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.request.RequestDto;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.service.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client")
public class ClientController {
    private final ClientService clientService;
    private final RequestService requestService;
    private final ContractService contractService;
    private final RecordService recordService;
    private final BillService billService;

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

    @PostMapping("/verify-account")
    public ServerResponseDto verifyAccount(@RequestParam String username,
                                           @RequestParam MultipartFile file) {
        return requestService.requestVerify(username, file);
    }

    @GetMapping("/requests")
    public ServerResponseDto getRequests(@RequestParam String username) {
        return requestService.getRequestsForClient(username);
    }

    @PostMapping("/create-request")
    public ServerResponseDto createRequest(@RequestParam String username,
                                           @RequestBody RequestDto dto) {
        return requestService.createRequest(username, dto);
    }

    @GetMapping("/contracts")
    public ServerResponseDto getContracts(@RequestParam String username){
        return contractService.getContractClientOwner(username);
    }

    @GetMapping("/records/7days-before")
    public ServerResponseDto getRecords7DaysBefore(@RequestParam String acronym){
        return recordService.findConsumeTime7DaysBefore(acronym);
    }

    @GetMapping("/records/6months-before")
    public ServerResponseDto getTotals6MonthsBefore(@RequestParam String acronym){
        return recordService.findConsume6MonthsBefore(acronym);
    }

    @GetMapping("/records/current-month")
    public ServerResponseDto getRecordsCurrentMonth(@RequestParam String acronym){
        return recordService.findRecordsCurrentMonth(acronym);
    }

    @GetMapping("/records/predict-current")
    public ServerResponseDto getPredictCurrentMonth(@RequestParam String acronym){
        return recordService.predictValueCurrentMonth(acronym);
    }

    @GetMapping("/bills")
    public ServerResponseDto getBillsContract(@RequestParam String contractName){
        return billService.getBillsContract(contractName);
    }
}
