package org.ecp.backend.service;

import org.ecp.backend.dto.CompanyDto;
import org.ecp.backend.dto.response.ServerResponseDto;

public interface CompanyService {
    ServerResponseDto create(CompanyDto dto);

    ServerResponseDto getCompanies();

    ServerResponseDto getCompany(String acronym);
}
