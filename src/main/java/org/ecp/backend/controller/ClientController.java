package org.ecp.backend.controller;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.service.ClientService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client")
public class ClientController {
    private final ClientService clientService;
    @PostMapping("/{username}/update-info")
    public ServerResponseDto updateInfo(@PathVariable String username, @RequestBody UserInfoDto dto) {
        return clientService.updateInfo(username, dto);
    }

    @PostMapping("/{username}/change-password")
    public ServerResponseDto changePassword(@PathVariable String username, @RequestBody PasswordRequest dto) {
        return clientService.changePassword(username, dto);
    }
}
