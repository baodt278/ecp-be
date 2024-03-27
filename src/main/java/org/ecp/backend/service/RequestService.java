package org.ecp.backend.service;

import org.ecp.backend.dto.request.AcceptRequest;
import org.ecp.backend.dto.request.ActionDto;
import org.ecp.backend.dto.request.RequestDto;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface RequestService {

    ServerResponseDto requestVerify(String username, MultipartFile file);

    ServerResponseDto getRequestVerify();

    ServerResponseDto verify(String username, ActionDto dto);

    ServerResponseDto create(String username, RequestDto dto);

    ServerResponseDto review(String username, ActionDto dto);

    ServerResponseDto accept(String username, AcceptRequest dto);
}
