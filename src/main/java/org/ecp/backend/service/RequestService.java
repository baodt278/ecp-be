package org.ecp.backend.service;

import org.ecp.backend.dto.request.*;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface RequestService {

    ServerResponseDto getRequestsForStaff(String acronym);

    ServerResponseDto getRequestsForManager(String acronym);

    ServerResponseDto getRequestsForClient(String username);

    ServerResponseDto getRequestsForAdmin();

    ServerResponseDto requestVerify(String username, DumbDto dto);

    ServerResponseDto verifyRequest(String username, ActionDto dto);

    ServerResponseDto createRequest(String username, RequestDto dto);

    ServerResponseDto updateRequest(String username, UpdateRequestDto dto);

    ServerResponseDto deleteRequest(String username, String code);

    ServerResponseDto reviewRequest(String username, ActionDto dto);

    ServerResponseDto acceptRequest(String username, AcceptRequest dto);
}
