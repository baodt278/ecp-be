package org.ecp.backend.service;

import org.ecp.backend.dto.response.ServerResponseDto;

public interface ContractService {
    ServerResponseDto getContractClientOwner(String username);

    ServerResponseDto getContractCompanySign(String acronym);

    ServerResponseDto getContractInfo(String contractName);
}
