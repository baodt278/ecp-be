package org.ecp.backend.controller;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.dto.PaymentDto;
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
    private final NewsService newsService;
    private final BaseService baseService;

    @GetMapping("/info")
    public ServerResponseDto getInfo(@RequestParam String username) {
        return clientService.getInfo(username);
    }


    @GetMapping("/info-detail")
    public ServerResponseDto getInfoDetail(@RequestParam String username) {
        return clientService.getDetailInfo(username);
    }

    @PostMapping("/update-info")
    public ServerResponseDto updateInfo(@RequestParam String username,
                                        @RequestBody UserInfoDto dto) {
        return clientService.updateInfo(username, dto);
    }

    @PostMapping(value = "/upload-avatar", consumes = {"multipart/form-data"})
    public ServerResponseDto uploadAvatar(@RequestParam String username,
                                          @ModelAttribute DumbDto dto) {
        return clientService.uploadAvatar(username, dto);
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
    public ServerResponseDto getRequestsVerify(String username) {
        return requestService.getRequestsVerifyForClient(username);
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
    public ServerResponseDto getRecords7DaysBefore(@RequestParam String contractName, @RequestParam String date) {
        return recordService.findConsumeTime7DaysBefore(contractName, date);
    }

    @GetMapping("/records/6months-before")
    public ServerResponseDto getTotals6MonthsBefore(@RequestParam String contractName, @RequestParam String date) {
        return recordService.findConsume6MonthsBefore(contractName, date);
    }

    @GetMapping("/records/current-month")
    public ServerResponseDto getRecordsCurrentMonth(@RequestParam String contractName, @RequestParam String date) {
        return recordService.findRecordsCurrentMonth(contractName, date);
    }

    @GetMapping("/records/predict-current")
    public ServerResponseDto getPredictCurrentMonth(@RequestParam String contractName, @RequestParam String date) {
        return recordService.predictValueCurrentMonth(contractName, date);
    }

    @GetMapping("/bills")
    public ServerResponseDto getBillsContract(@RequestParam String contractName) {
        return billService.getBillsContract(contractName);
    }

    @GetMapping("/bills/current-month")
    public ServerResponseDto getBillCurrentMonth(@RequestParam String username, @RequestParam String date) {
        return billService.getBillCurrentMonth(username, date);
    }

    @GetMapping("/news/system")
    public ServerResponseDto getSystemNews() {
        return newsService.getSystemNews();
    }

    @GetMapping("/news/local")
    public ServerResponseDto getLocalNewsForClient(@RequestParam String username) {
        return newsService.getLocalNewsForUser(username);
    }

    @PostMapping(value = "/payment", consumes = {"multipart/form-data"})
    public ServerResponseDto paymentRequest(@RequestParam String username, @ModelAttribute PaymentDto dto) {
        return requestService.paymentRequest(username, dto);
    }

    @GetMapping("/charge")
    public ServerResponseDto getCharge(@RequestParam String username) {
        return contractService.getCharge(username);
    }

    @GetMapping("/message")
    public ServerResponseDto getMessage() {
        return baseService.getMessage();
    }

}
