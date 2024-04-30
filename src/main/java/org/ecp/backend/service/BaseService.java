package org.ecp.backend.service;

import org.ecp.backend.dto.BaseDto;
import org.ecp.backend.dto.response.ServerResponseDto;

public interface BaseService {
    ServerResponseDto getAll();
    ServerResponseDto create(BaseDto dto);

    ServerResponseDto update(BaseDto dto);

    ServerResponseDto delete(String object);
}
