package org.ecp.backend.controller;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.dto.UserInfoDto;
import org.ecp.backend.dto.request.PasswordRequest;
import org.ecp.backend.dto.request.RequestDto;
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
        return requestService.verifyClient(username, file);
    }

    @GetMapping("/requests")
    public ServerResponseDto getRequests(@RequestParam String username) {
        return requestService.getRequestsForClient(username);
    }

    @PostMapping("/create-request")
    public ServerResponseDto createRequest(@RequestParam String username,
                                           @RequestBody RequestDto dto) {
        return requestService.create(username, dto);
    }
}
