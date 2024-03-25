package org.ecp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.ecp.backend.Constant.CommonConstant;
import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.request.LoginRequest;
import org.ecp.backend.dto.request.RegisterRequest;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.DetailDto;
import org.ecp.backend.dto.response.ResponseDto;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.entity.Client;
import org.ecp.backend.enums.Role;
import org.ecp.backend.exception.ApplicationRuntimeException;
import org.ecp.backend.repository.ClientRepository;
import org.ecp.backend.service.ClientService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepo;
    private final PasswordEncoder encoder;

    @Override
    public ServerResponseDto login(LoginRequest dto) {
        Client client = clientRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Tai khoan khong ton tai"));
        if (!encoder.matches(dto.getPassword(), client.getPassword()))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Sai tai khoan hoac mat khau");
        ResponseDto responseDto = new ResponseDto(client.getUsername(), client.getRole());
        return new ServerResponseDto(CommonConstant.SUCCESS, responseDto);
    }

    @Override
    public ServerResponseDto register(RegisterRequest dto) {
        String username = dto.getUsername();
        String email = dto.getEmail();
        if (clientRepo.existsByUsername(username))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Username da ton tai");
        if (clientRepo.existsByEmail(email))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Email da duoc su dung");
        Client client = Client.builder()
                .username(username)
                .password(encoder.encode(dto.getPassword()))
                .email(email)
                .role(Role.CLIENT)
                .active(false)
                .build();
        clientRepo.save(client);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Tao tai khoan thanh cong");
    }

    @Override
    public ServerResponseDto changePassword(String username, PasswordRequest dto) {
        Client client = clientRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong tim thay tai khoan"));
        if (!encoder.matches(dto.getOldPassword(), client.getPassword()))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Sai mat khau");
        client.setPassword(encoder.encode(dto.getNewPassword()));
        clientRepo.save(client);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Thay doi mat khau thanh cong");
    }

    @Override
    public ServerResponseDto updateInfo(String username, UserInfoDto dto) {
        Client client = clientRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong tim thay tai khoan"));
        String email = dto.getEmail();
        if (StringUtils.isNotBlank(email) || clientRepo.existsByEmail(email))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Email da duoc su dung");
        client.setEmail(StringUtils.defaultIfEmpty(email, client.getEmail()));
        client.setPhone(StringUtils.defaultIfEmpty(dto.getPhone(), client.getPhone()));
        client.setAddress(StringUtils.defaultIfEmpty(dto.getAddress(), client.getAddress()));
        client.setFullName(StringUtils.defaultIfEmpty(dto.getFullName(), client.getFullName()));
        client.setAvatar(StringUtils.defaultIfEmpty(dto.getAvatar(), client.getAvatar()));
        clientRepo.save(client);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Thay doi thong tin thanh cong");
    }

    @Override
    public ServerResponseDto getInfo(String username) {
        Client client = clientRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong tim thay tai khoan"));
        UserInfoDto infoDto = new UserInfoDto(client.getEmail(), client.getPhone(), client.getAddress(), client.getFullName(), client.getAvatar());
        return new ServerResponseDto(CommonConstant.SUCCESS, infoDto);
    }

    @Override
    public ServerResponseDto getDetailInfo(String username){
        Client client = clientRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong tim thay tai khoan"));
        DetailDto dto = new DetailDto(client.getPersonId(), client.getPersonName(), client.getDateOfBirth(), client.getActive());
        return new ServerResponseDto(CommonConstant.SUCCESS, dto);
    }
    
//    public ServerResponseDto verifyClient(String username, boolean decision, DetailDto dto){
//        Client client = clientRepo.findByUsername(username)
//                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong tim thay tai khoan"));
//    }
}
