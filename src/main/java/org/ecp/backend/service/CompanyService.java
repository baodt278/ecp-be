package org.ecp.backend.service;

import org.ecp.backend.dto.response.CompanyResponse;
import org.ecp.backend.dto.response.ServerResponseDto;

public interface CompanyService {
    ServerResponseDto create(CompanyResponse dto);

    ServerResponseDto getCompanies();

    ServerResponseDto getCompany(String acronym);
}
