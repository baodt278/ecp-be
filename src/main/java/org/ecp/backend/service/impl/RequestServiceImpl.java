package org.ecp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.ecp.backend.constant.CommonConstant;
import org.ecp.backend.dto.PaymentDto;
import org.ecp.backend.dto.request.*;
import org.ecp.backend.dto.response.RequestResponse;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.entity.*;
import org.ecp.backend.enums.*;
import org.ecp.backend.exception.ApplicationRuntimeException;
import org.ecp.backend.repository.*;
import org.ecp.backend.service.MailService;
import org.ecp.backend.service.MinioService;
import org.ecp.backend.service.RequestService;
import org.ecp.backend.utils.DateUtils;
import org.ecp.backend.utils.GenerateUtils;
import org.ecp.backend.utils.TextUtils;
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
import java.util.*;

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
    private final MailService mailService;
    private final BillRepository billRepo;
    @Value("${id_extract_url}")
    private String url;
    @Value("${file_path}")
    private String filePath;

    @Override
    public ServerResponseDto getRequestsForAdmin() {
        List<Request> requests = requestRepo.findRequestsBy(RequestType.CLIENT_VERIFY, RequestStatus.PENDING);
        List<RequestResponse> responses = requests.stream().map(this::getRequestResponse).toList();
        return new ServerResponseDto(CommonConstant.SUCCESS, responses);
    }

    @Override
    public ServerResponseDto getRequestForCompany(String acronym) {
        List<Request> requests = requestRepo.findRequestsByCompanyAcronym(acronym);
        List<RequestResponse> responses = requests.stream().map(this::getRequestResponse).toList();
        return new ServerResponseDto(CommonConstant.SUCCESS, responses);
    }

    @Override
    public ServerResponseDto getRequestsVerifyForClient(String username) {
        List<Request> requests = requestRepo.findVerifyRequestsByClient(username);
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
        List<Request> requests = requestRepo.findRequestsForClient(username);
        List<RequestResponse> responses = requests.stream().map(this::getRequestResponse).toList();
        return new ServerResponseDto(CommonConstant.SUCCESS, responses);
    }

    @Override
    public ServerResponseDto requestVerify(String username, DumbDto dto) {
        try {
            Client client = clientRepo.findByUsername(username)
                    .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!"));
            Request request = Request.builder()
                    .code(GenerateUtils.generatedCode())
                    .description(CommonConstant.DESCRIPTION_VERIFY)
                    .createdAt(new Date())
                    .images(Collections.singletonList(minioService.uploadFile(dto.getFiles()[0])))
                    .type(RequestType.CLIENT_VERIFY)
                    .status(RequestStatus.PENDING)
                    .info(getDataFromIdExtract(dto.getFiles()[0]))
                    .client(client)
                    .build();
            String personId = convertJsonToArray(request.getInfo())[0];
            if (clientRepo.existsByPersonId(personId))
                throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "CCCD đã được sử dụng!");
            requestRepo.save(request);
            return new ServerResponseDto(CommonConstant.SUCCESS, "Đã gửi yêu cầu xác minh tài khoản!");
        } catch (Exception e) {
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Lỗi khi gửi yêu cầu xác minh tài khoản!");
        }
    }

    @Override
    public ServerResponseDto verifyRequest(String username, ActionDto dto) {
        if (!adminRepo.existsByUsername(username))
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!");
        Request request = requestRepo.findRequestByCode(dto.getCode())
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy yêu cầu"));
        request.setAcceptedBy(username);
        request.setAcceptedAt(new Date());
        request.setAcceptText(dto.getText());
        request.setStatus(RequestStatus.valueOf(dto.getStatus()));
        requestRepo.save(request);
        updateInfoClient(request);
        mailService.sendMail("doantuanbao2708@gmail.com", "Hệ thống điện", request.getClient().getEmail(), "Xác minh tài khoản", request.getStatus() == RequestStatus.APPROVED ? """
                Tài khoản của bạn đã được xác minh!
                Đăng nhập tài khoản tại đây http://localhost:3000/client-login""" : "Tài khoản của bạn chưa được xác minh!\nVui long kiểm tra lại thông tin!");
        return new ServerResponseDto(CommonConstant.SUCCESS, "Đã xác minh yêu cầu!");
    }

    @Override
    public ServerResponseDto createRequest(String username, RequestDto dto) {
        try {
            Client client = clientRepo.findByUsername(username)
                    .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!"));
            Company company;
            if (!dto.getAcronymCompany().isEmpty()) {
                company = companyRepo.findByAcronym(dto.getAcronymCompany())
                        .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy công ty!"));
            } else {
                String contractName = dto.getInfo().split("\\|")[0];
                Contract contract = contractRepo.findByName(contractName)
                        .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy hợp đồng!"));
                company = contract.getCompany();
            }
            Request request = Request.builder()
                    .code(GenerateUtils.generatedCode())
                    .description(dto.getDescription())
                    .createdAt(new Date())
                    .images(dto.getImages() == null ? null : Arrays.stream(dto.getImages()).map(minioService::uploadFile).toList())
                    .type(RequestType.valueOf(dto.getType()))
                    .status(!Objects.equals(dto.getType(), "EMERGENCY") ? RequestStatus.PENDING : RequestStatus.REVIEWED)
                    .info(dto.getInfo())
                    .company(company)
                    .client(client)
                    .build();
            requestRepo.save(request);
            return new ServerResponseDto(CommonConstant.SUCCESS, "Đã tạo yêu cầu mới!");
        } catch (Exception e) {
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Lỗi khi tạo yêu cầu!");
        }
    }

    @Override
    public ServerResponseDto updateRequest(String username, UpdateRequestDto dto) {
        try {
            if (!clientRepo.existsByUsername(username))
                throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!");
            Request request = requestRepo.findRequestByCode(dto.getCode())
                    .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy yêu cầu!"));
            request.setDescription(StringUtils.defaultIfEmpty(dto.getDescription(), request.getDescription()));
            request.setImages(dto.getImages() == null ? request.getImages() : Arrays.stream(dto.getImages()).map(minioService::uploadFile).toList());
            requestRepo.save(request);
            return new ServerResponseDto(CommonConstant.SUCCESS, "Đã cập nhật yêu cầu!");
        } catch (Exception e) {
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Lỗi khi cập nhật yêu cầu!");
        }
    }

    @Override
    public ServerResponseDto deleteRequest(String username, String code) {
        try {
            if (!clientRepo.existsByUsername(username))
                throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!");
            Request request = requestRepo.findRequestByCode(code)
                    .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy yêu cầu!"));
            requestRepo.delete(request);
            return new ServerResponseDto(CommonConstant.SUCCESS, "Đã hủy yêu cầu!");
        } catch (Exception e) {
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Lỗi khi hủy yêu cầu!");
        }
    }

    @Override
    public ServerResponseDto reviewRequest(String username, ActionDto dto) {
        if (!employeeRepo.existsByUsername(username))
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!");
        Request request = requestRepo.findRequestByCode(dto.getCode())
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy yêu cầu"));
        request.setReviewedBy(username);
        request.setReviewedAt(new Date());
        request.setReviewText(dto.getText());
        request.setStatus(RequestStatus.valueOf(dto.getStatus()));
        if (request.getType() == RequestType.PAYMENT) {
            Bill bill = billRepo.findByCode(request.getInfo())
                    .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy hóa đơn!"));
            bill.setStatus(request.getStatus() == RequestStatus.REVIEWED ? BillStatus.PAID :
              (bill.getEnd().before(new Date()) ? BillStatus.EXPIRED : BillStatus.UNPAID));
            billRepo.save(bill);
            request.setStatus(RequestStatus.APPROVED);
        }
        requestRepo.save(request);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Đã xác minh yêu cầu");
    }

    @Override
    public ServerResponseDto acceptRequest(String username, AcceptRequest dto) {
        List<ChargeDto> charges = dto.getCharges();
        if (!employeeRepo.existsByUsername(username))
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!");
        Request request = requestRepo.findRequestByCode(dto.getCode())
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy yêu cầu"));
        request.setAcceptedBy(username);
        request.setAcceptedAt(new Date());
        request.setAcceptText(dto.getText());
        request.setStatus(RequestStatus.valueOf(dto.getStatus()));
        requestRepo.save(request);
        effectByRequest(request, charges);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Đã xác nhận yêu cầu!");
    }

    private void updateInfoClient(Request request) {
        //["id","name","date",....]
        if (request.getStatus() != RequestStatus.APPROVED) return;
        String[] data = convertJsonToArray(request.getInfo());
        Client client = request.getClient();
        if (clientRepo.existsByPersonId(data[0])) {
            request.setStatus(RequestStatus.REJECTED);
            requestRepo.save(request);
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "CCCD đã được sử dụng!");
        }
        client.setPersonId(data[0]); // ID
        client.setPersonName(data[1]); // Full name
        client.setDateOfBirth(DateUtils.convertStringToDate(data[2], "dd/MM/yyyy")); // Date of birth
        client.setActive(true);
        clientRepo.save(client);
    }

    private void effectByRequest(Request request, List<ChargeDto> dtos) {
        // "name"|"address"|"houses"|"type"|"status"|"volt"
        if (request.getStatus() != RequestStatus.APPROVED) return;
        RequestType type = request.getType();
        Company company = request.getCompany();
        Client client = request.getClient();
        String[] data = request.getInfo().split("\\|");
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
                        .houses(ObjectUtils.isEmpty(data[2]) ? 0 : Integer.parseInt(data[2]))
                        .type(ContractType.valueOf(data[3]))
                        .status(ContractStatus.ACTIVE)
                        .volt(Volt.valueOf(data[5]))
                        .company(company)
                        .client(client)
                        .createdAt(new Date())
                        .build();
                contractRepo.save(contract);
                contractName = contract.getName();
                TextUtils.createTextFile(filePath, contractName);
            }
            case CONTRACT_CHANGE -> {
                Contract contract = contractRepo.findByName(data[0])
                        .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tồn tại hợp đồng! nay"));
                contract.setAddress(StringUtils.defaultIfEmpty(data[1], contract.getAddress()));
                contract.setHouses(!StringUtils.isEmpty(data[2]) ? Integer.parseInt(data[2]) : contract.getHouses());
                contract.setCreatedAt(new Date());
                contract.setType(!StringUtils.isEmpty(data[3]) ? ContractType.valueOf(data[3]) : contract.getType());
                contract.setVolt(!StringUtils.isEmpty(data[5]) ? Volt.valueOf(data[5]) : contract.getVolt());
                contract.setUpdatedAt(new Date());
                contractRepo.save(contract);
                contractName = contract.getName();
            }
            case CONTRACT_STATUS -> {
                Contract contract = contractRepo.findByName(data[0])
                        .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tồn tại hợp đồng! nay"));
                contract.setStatus(!StringUtils.isEmpty(data[4]) ? ContractStatus.valueOf(data[4]) : contract.getStatus());
                contract.setUpdatedAt(new Date());
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
        if (response.getStatusCode() != HttpStatus.OK)
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không thể nhận diện được CCCD!");
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

    @Override
    public ServerResponseDto paymentRequest(String username, PaymentDto dto) {
        if (!clientRepo.existsByUsername(username))
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!");
        Bill bill = billRepo.findByCode(dto.getBillCode())
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy hóa đơn!"));
        if (bill.getStatus() == BillStatus.PAID)
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Hóa đơn này đã được thanh toán!");
        if (bill.getEnd().before(new Date()))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Hóa đơn này đã quá hạn!");
        Request request = Request.builder()
                .code(GenerateUtils.generatedCode())
                .description(CommonConstant.DESCRIPTION_PAYMENT + bill.getCode())
                .createdAt(new Date())
                .images(dto.getImages() == null ? null : Arrays.stream(dto.getImages()).map(minioService::uploadFile).toList())
                .type(RequestType.PAYMENT)
                .status(RequestStatus.PENDING)
                .info(bill.getCode())
                .company(bill.getContract().getCompany())
                .client(bill.getContract().getClient())
                .build();
        requestRepo.save(request);
        bill.setStatus(BillStatus.PENDING);
        billRepo.save(bill);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Đã gửi yêu cầu thanh toán!");
    }
}
