package org.ecp.backend.controller;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.dto.BaseDto;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.request.*;
import org.ecp.backend.dto.CompanyDto;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.service.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"*"})
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final EmployeeService employeeService;
    private final CompanyService companyService;
    private final RequestService requestService;
    private final BaseService baseService;
    private final PriceService priceService;
    private final ContractService contractService;
    private final NewsService newsService;

    @GetMapping("/info")
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
        return adminService.getEmployees(acronym);
    }

    @GetMapping("/contracts")
    public ServerResponseDto getContracts(@RequestParam String acronym) {
        return contractService.getContractCompanySign(acronym);
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

    @PostMapping(value = "/upload-avatar", consumes = {"multipart/form-data"})
    public ServerResponseDto uploadAvatar(@RequestParam String username,
                                          @ModelAttribute DumbDto dto) {
        return adminService.uploadAvatar(username, dto);
    }

    @PostMapping("/update-company")
    public ServerResponseDto updateCompany(@RequestBody CompanyDto dto) {
        return companyService.update(dto);
    }

    @PostMapping("/delete-company")
    public ServerResponseDto deleteCompany(@RequestParam String acronym) {
        return companyService.delete(acronym);
    }

    @GetMapping("/bases")
    public ServerResponseDto getBase() {
        return baseService.getAll();
    }

    @PostMapping("/create-base")
    public ServerResponseDto createBase(@RequestBody BaseDto dto) {
        return baseService.create(dto);
    }

    @PostMapping("/update-base")
    public ServerResponseDto updateBase(@RequestBody BaseDto dto) {
        return baseService.update(dto);
    }

    @PostMapping("/delete-base")
    public ServerResponseDto deleteBase(@RequestParam String object) {
        return baseService.delete(object);
    }

    @GetMapping("/prices")
    public ServerResponseDto getPrice() {
        return priceService.getAll();
    }

    @PostMapping("/update-price")
    public ServerResponseDto updatePrice(@RequestParam long id, @RequestParam double value) {
        return priceService.update(id, value);
    }

    @PostMapping("/delete-price")
    public ServerResponseDto deletePrice(@RequestParam long id) {
        return priceService.delete(id);
    }

    @GetMapping("/news/system")
    public ServerResponseDto getNews() {
        return newsService.getSystemNews();
    }

    @GetMapping("/news/local")
    public ServerResponseDto getLocalNews() {
        return newsService.getLocalNewsAll();
    }

    @PostMapping(value = "/news/create-global", consumes = {"multipart/form-data"})
    public ServerResponseDto createGlobalNews(@ModelAttribute NewsRequest dto) {
        return newsService.createGlobalNews(dto);
    }

    @PostMapping("/news/delete")
    public ServerResponseDto deleteNews(@RequestParam String code) {
        return newsService.deleteNews(code);
    }
}
