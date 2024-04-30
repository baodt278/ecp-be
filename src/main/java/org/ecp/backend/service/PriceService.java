package org.ecp.backend.service;

import org.ecp.backend.dto.response.ServerResponseDto;

public interface PriceService {
    ServerResponseDto getAll();
    ServerResponseDto update(long id, double value);
    ServerResponseDto delete(long id);
}
