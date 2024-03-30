package org.ecp.backend.service;

import org.ecp.backend.dto.request.AcceptRequest;
import org.ecp.backend.dto.request.ActionDto;
import org.ecp.backend.dto.request.RequestDto;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface RequestService {

    ServerResponseDto getRequestsForStaff(String acronym);

    ServerResponseDto getRequestsForManager(String acronym);

    ServerResponseDto getRequestsForClient(String username);

    ServerResponseDto requestVerify(String username, MultipartFile file);

    ServerResponseDto getRequestsForAdmin();

    ServerResponseDto verifyRequest(String username, ActionDto dto);

    ServerResponseDto createRequest(String username, RequestDto dto);

    ServerResponseDto reviewRequest(String username, ActionDto dto);

    ServerResponseDto acceptRequest(String username, AcceptRequest dto);
}
