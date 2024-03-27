package org.ecp.backend.controller;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.service.ClientService;
import org.ecp.backend.service.RequestService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client")
public class ClientController {
    private final ClientService clientService;
    private final RequestService requestService;
    @PostMapping("/{username}/update-info")
    public ServerResponseDto updateInfo(@PathVariable String username, @RequestBody UserInfoDto dto) {
        return clientService.updateInfo(username, dto);
    }

    @PostMapping("/{username}/change-password")
    public ServerResponseDto changePassword(@PathVariable String username, @RequestBody PasswordRequest dto) {
        return clientService.changePassword(username, dto);
    }

    @PostMapping("/{username}/verify-account")
    public ServerResponseDto verifyAccount(@PathVariable String username, @RequestParam("file") MultipartFile file) {
        return requestService.requestVerify(username, file);
    }
}
