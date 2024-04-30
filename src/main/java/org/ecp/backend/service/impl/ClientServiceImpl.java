package org.ecp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.ecp.backend.constant.CommonConstant;
import org.ecp.backend.dto.request.DumbDto;
import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.request.LoginRequest;
import org.ecp.backend.dto.request.RegisterRequest;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.DetailDto;
import org.ecp.backend.dto.response.ClientResponse;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.entity.Client;
import org.ecp.backend.enums.Role;
import org.ecp.backend.exception.ApplicationRuntimeException;
import org.ecp.backend.repository.ClientRepository;
import org.ecp.backend.service.ClientService;
import org.ecp.backend.service.MinioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepo;
    private final PasswordEncoder encoder;
    private final MinioService minioService;

    @Override
    public ServerResponseDto login(LoginRequest dto) {
        Client client = clientRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Tài khoản không tồn tại!"));
        if (!encoder.matches(dto.getPassword(), client.getPassword()))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Tài khoản hoặc mật khẩu không đúng!");
        ClientResponse response = new ClientResponse(client.getUsername(), client.getRole(), client.getActive(), minioService.getUrl(client.getAvatar()));
        return new ServerResponseDto(CommonConstant.SUCCESS, response);
    }

    @Override
    public ServerResponseDto register(RegisterRequest dto) {
        String username = dto.getUsername();
        String email = dto.getEmail();
        if (clientRepo.existsByUsername(username))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Tên đăng nhập đã tồn tại!");
        if (clientRepo.existsByEmail(email))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Email đã được sử dụng!");
        Client client = Client.builder()
                .username(username)
                .password(encoder.encode(dto.getPassword()))
                .email(email)
                .role(Role.CLIENT)
                .active(false)
                .build();
        clientRepo.save(client);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Tạo tài khoản thành công!");
    }

    @Override
    public ServerResponseDto changePassword(String username, PasswordRequest dto) {
        Client client = clientRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!"));
        if (!encoder.matches(dto.getOldPassword(), client.getPassword()))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Mật khẩu không đúng");
        client.setPassword(encoder.encode(dto.getNewPassword()));
        clientRepo.save(client);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Thay đổi mật khẩu thành công!");
    }

    @Override
    public ServerResponseDto updateInfo(String username, UserInfoDto dto) {
        Client client = clientRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!"));
        String email = dto.getEmail();
        if (clientRepo.existsByEmail(email) && !client.getEmail().equals(email))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Email đã được sử dụng!");
        client.setEmail(StringUtils.defaultIfEmpty(email, client.getEmail()));
        client.setPhone(StringUtils.defaultIfEmpty(dto.getPhone(), client.getPhone()));
        client.setAddress(StringUtils.defaultIfEmpty(dto.getAddress(), client.getAddress()));
        clientRepo.save(client);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Thay đổi thông tin thành công!");
    }

    @Override
    public ServerResponseDto uploadAvatar(String username, DumbDto dto) {
        Client client = clientRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!"));
        client.setAvatar(minioService.uploadFile(dto.getFiles()[0]));
        clientRepo.save(client);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Thay đổi ảnh đại diện thành công!");
    }

    @Override
    public ServerResponseDto getInfo(String username) {
        Client client = clientRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!"));
        UserInfoDto infoDto = new UserInfoDto(client.getEmail(), client.getPhone(), client.getAddress(), client.getFullName(), minioService.getUrl(client.getAvatar()));
        return new ServerResponseDto(CommonConstant.SUCCESS, infoDto);
    }

    @Override
    public ServerResponseDto getDetailInfo(String username) {
        Client client = clientRepo.findByUsername(username)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tài khoản!"));
        DetailDto dto = new DetailDto(client.getPersonId(), client.getPersonName(), client.getDateOfBirth());
        return new ServerResponseDto(CommonConstant.SUCCESS, dto);
    }
}
