package org.ecp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.ecp.backend.constant.CommonConstant;
import org.ecp.backend.dto.request.AcceptRequest;
import org.ecp.backend.dto.request.ActionDto;
import org.ecp.backend.dto.request.ChargeDto;
import org.ecp.backend.dto.request.RequestDto;
import org.ecp.backend.dto.response.RequestResponse;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.entity.*;
import org.ecp.backend.enums.*;
import org.ecp.backend.exception.ApplicationRuntimeException;
import org.ecp.backend.repository.*;
import org.ecp.backend.service.MinioService;
import org.ecp.backend.service.RequestService;
import org.ecp.backend.utils.DateUtils;
import org.ecp.backend.utils.GenerateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepo;
    private final ClientRepository clientRepo;
    private final CompanyRepository companyRepo;
    private final AdminRepository adminRepo;
    private final EmployeeRepository employeeRepo;
    private final ContractRepository contractRepo;
    private final ChargeRepository chargeRepo;
    private final MinioService minioService;
    @Value("${id_extract_url}")
    private String url;

    @Override
    public ServerResponseDto getRequestsForAdmin() {
        List<Request> requests = requestRepo.findRequestsBy(RequestType.CLIENT_VERIFY, RequestStatus.PENDING);
        List<RequestResponse> responses = requests.stream().map(this::getRequestResponse).toList();
        return new ServerResponseDto(CommonConstant.SUCCESS, responses);
    }

    @Override
    public ServerResponseDto getRequestsForStaff(String acronym) {
        List<RequestType> types = List.of(RequestType.CLIENT_VERIFY, RequestType.EMERGENCY);
        List<Request> requests = requestRepo.findRequestsCompany(acronym, types, RequestStatus.PENDING);
        List<RequestResponse> responses = requests.stream().map(this::getRequestResponse).toList();
        return new ServerResponseDto(CommonConstant.SUCCESS, responses);
    }

    @Override
    public ServerResponseDto getRequestsForManager(String acronym) {
        List<RequestType> types = List.of(RequestType.ADVICE, RequestType.QUESTION, RequestType.CLIENT_VERIFY);
        List<Request> requests = requestRepo.findRequestsCompany(acronym, types, RequestStatus.REVIEWED);
        List<RequestResponse> responses = requests.stream().map(this::getRequestResponse).toList();
        return new ServerResponseDto(CommonConstant.SUCCESS, responses);
    }

    @Override
    public ServerResponseDto getRequestsForClient(String username) {
        List<Request> requests = requestRepo.findRequestsByClient_Username(username);
        List<RequestResponse> responses = requests.stream().map(this::getRequestResponse).toList();
        return new ServerResponseDto(CommonConstant.SUCCESS, responses);
    }

    @Override
    public ServerResponseDto requestVerify(String username, MultipartFile file) {
        try {
            Client client = clientRepo.findByUsername(username)
                    .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong tim thay tai khoan"));
            Request request = Request.builder()
                    .code(GenerateUtils.generatedCode())
                    .description(CommonConstant.DESCRIPTION_VERIFY)
                    .createdAt(new Date())
                    .images(Collections.singletonList(minioService.uploadFile(file)))
                    .type(RequestType.CLIENT_VERIFY)
                    .status(RequestStatus.PENDING)
                    .info(getDataFromIdExtract(file))
                    .client(client)
                    .build();
            requestRepo.save(request);
            return new ServerResponseDto(CommonConstant.SUCCESS, "Da gui yeu cau xac minh tai khoan");
        } catch (Exception e) {
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Loi khi tao yeu cau xac minh");
        }
    }

    @Override
    public ServerResponseDto verifyRequest(String username, ActionDto dto) {
        if (!adminRepo.existsByUsername(username))
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong tim thay tai khoan");
        Request request = requestRepo.findRequestByCode(dto.getCode())
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong tim thay yeu cau"));
        request.setAcceptedBy(username);
        request.setAcceptedAt(new Date());
        request.setAcceptText(dto.getText());
        request.setStatus(dto.getStatus());
        requestRepo.save(request);
        updateInfoClient(request);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Da thuc hien xac minh tai khoan");
    }

    @Override
    public ServerResponseDto createRequest(String username, RequestDto dto) {
        try {
            Client client = clientRepo.findByUsername(username)
                    .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong tim thay tai khoan"));
            Company company = companyRepo.findByAcronym(dto.getAcronymCompany())
                    .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong tim thay cong ty"));
            Request request = Request.builder()
                    .code(GenerateUtils.generatedCode())
                    .description(dto.getDescription())
                    .createdAt(new Date())
                    .images(dto.getImages() == null ? null : dto.getImages().stream().map(minioService::uploadFile).toList())
                    .type(dto.getType())
                    .status(dto.getType() != RequestType.EMERGENCY ? RequestStatus.PENDING : RequestStatus.REVIEWED)
                    .info(dto.getInfo())
                    .company(company)
                    .client(client)
                    .build();
            requestRepo.save(request);
            return new ServerResponseDto(CommonConstant.SUCCESS, "Da gui yeu cau");
        } catch (Exception e) {
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Loi khi tao yeu cau");
        }
    }

    @Override
    public ServerResponseDto reviewRequest(String username, ActionDto dto) {
        if (!employeeRepo.existsByUsername(username))
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong tim thay tai khoan");
        Request request = requestRepo.findRequestByCode(dto.getCode())
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong tim thay yeu cau"));
        request.setReviewedBy(username);
        request.setReviewedAt(new Date());
        request.setReviewText(dto.getText());
        request.setStatus(dto.getStatus());
        requestRepo.save(request);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Da xac minh yeu cau");
    }

    @Override
    public ServerResponseDto acceptRequest(String username, AcceptRequest dto) {
        ActionDto action = dto.getAction();
        List<ChargeDto> charges = dto.getCharges();
        if (!employeeRepo.existsByUsername(username))
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong tim thay tai khoan");
        Request request = requestRepo.findRequestByCode(action.getCode())
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong tim thay yeu cau"));
        request.setAcceptedBy(username);
        request.setAcceptedAt(new Date());
        request.setAcceptText(action.getText());
        request.setStatus(action.getStatus());
        requestRepo.save(request);
        effectByRequest(request, charges);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Da xac minh yeu cau");
    }

    private void updateInfoClient(Request request) {
        //["id","name","date",....]
        if (request.getStatus() != RequestStatus.APPROVED) return;
        String[] data = convertJsonToArray(request.getInfo());
        Client client = request.getClient();
        client.setActive(true);
        client.setPersonId(data[0]); // ID
        client.setPersonName(data[1]); // Full name
        client.setDateOfBirth(DateUtils.convertStringToDate(data[2], "dd/MM/yyyy")); // Date of birth
        clientRepo.save(client);
    }

    private void effectByRequest(Request request, List<ChargeDto> dtos) {
        // "name","address","houses","type","status","volt"
        if (request.getStatus() != RequestStatus.APPROVED) return;
        RequestType type = request.getType();
        Company company = request.getCompany();
        Client client = request.getClient();
        String[] data = request.getInfo().split(",");
        if (type.toString().contains("CONTRACT_")) {
            String contractName = createOrUpdateContract(type, company, client, data);
            createCharges(dtos, contractName, request.getCode(), company);
        }
    }

    private String createOrUpdateContract(RequestType type, Company company, Client client, String[] data) {
        String contractName;
        switch (type) {
            case CONTRACT_NEW -> {
                Contract contract = Contract.builder()
                        .name(GenerateUtils.generateContract(company.getAcronym()))
                        .address(data[1])
                        .houses(Integer.valueOf(data[2]))
                        .type(ContractType.valueOf(data[3]))
                        .status(ContractStatus.ACTIVE)
                        .volt(Volt.valueOf(data[5]))
                        .company(company)
                        .client(client)
                        .build();
                contractRepo.save(contract);
                contractName = contract.getName();
            }
            case CONTRACT_CHANGE -> {
                Contract contract = contractRepo.findByName(data[0])
                        .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong ton tai hop dong nay"));
                contract.setAddress(StringUtils.defaultIfEmpty(data[1], contract.getAddress()));
                contract.setHouses(!StringUtils.isEmpty(data[2]) ? Integer.valueOf(data[2]) : contract.getHouses());
                contract.setCreatedAt(new Date());
                contract.setType(!StringUtils.isEmpty(data[3]) ? ContractType.valueOf(data[3]) : contract.getType());
                contract.setVolt(!StringUtils.isEmpty(data[5]) ? Volt.valueOf(data[5]) : contract.getVolt());
                contractRepo.save(contract);
                contractName = contract.getName();
            }
            case CONTRACT_STATUS -> {
                Contract contract = contractRepo.findByName(data[0])
                        .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong ton tai hop dong nay"));
                contract.setStatus(!StringUtils.isEmpty(data[4]) ? ContractStatus.valueOf(data[4]) : contract.getStatus());
                contractRepo.save(contract);
                contractName = contract.getName();
            }
            default -> {
                contractName = null;
            }
        }
        return contractName;
    }

    private void createCharges(List<ChargeDto> dtos, String contractName, String requestCode, Company company) {
        List<Charge> charges = new ArrayList<>();
        for (ChargeDto dto : dtos) {
            Charge charge = Charge.builder()
                    .contractName(contractName)
                    .requestCode(requestCode)
                    .createdAt(new Date())
                    .type(dto.getType())
                    .reason(dto.getReason())
                    .value(dto.getValue())
                    .company(company)
                    .build();
            charges.add(charge);
        }
        chargeRepo.saveAll(charges);
    }


    private RequestResponse getRequestResponse(Request request) {
        List<String> imageUrls = request.getImages() == null ? null :
                request.getImages().stream()
                        .map(minioService::getUrl)
                        .toList();
        return RequestResponse.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .createdAt(request.getCreatedAt())
                .imageUrls(imageUrls)
                .type(request.getType())
                .status(request.getStatus())
                .info(request.getInfo())
                .reviewedBy(request.getReviewedBy())
                .reviewedAt(request.getReviewedAt())
                .reviewText(request.getReviewText())
                .acceptedBy(request.getAcceptedBy())
                .acceptedAt(request.getAcceptedAt())
                .acceptText(request.getAcceptText())
                .companyAcronym(request.getCompany() != null ? request.getCompany().getAcronym() : null)
                .usernameClient(request.getClient().getUsername())
                .build();
    }

    private String getDataFromIdExtract(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(convert(file)));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        String responseData = response.getBody();
        return responseData.substring(responseData.indexOf("["), responseData.lastIndexOf("]") + 1);
    }

    private File convert(MultipartFile file) throws IOException {
        Path tempFile = Files.createTempFile("temp", file.getOriginalFilename());
        file.transferTo(tempFile.toFile());
        return tempFile.toFile();
    }

    private String[] convertJsonToArray(String json) {
        return json.replaceAll("[\\[\\]\"']", "").split(",");
    }
}
