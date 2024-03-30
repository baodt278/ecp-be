package org.ecp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.constant.CommonConstant;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.exception.ApplicationRuntimeException;
import org.ecp.backend.repository.ClientRepository;
import org.ecp.backend.repository.CompanyRepository;
import org.ecp.backend.repository.ContractRepository;
import org.ecp.backend.service.ContractService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {
    private final ClientRepository clientRepo;
    private final CompanyRepository companyRepo;
    private final ContractRepository contractRepo;
    @Override
    public ServerResponseDto getContractClientOwner(String username){
        if (!clientRepo.existsByUsername(username))
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong ton tai tai khoan nay");
        return new ServerResponseDto(CommonConstant.SUCCESS, contractRepo.findContractClientOwner(username));
    }

    @Override
    public ServerResponseDto getContractCompanySign(String acronym){
        if (!companyRepo.existsByAcronym(acronym))
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong ton tai tai khoan nay");
        return new ServerResponseDto(CommonConstant.SUCCESS, contractRepo.findContractCompanySign(acronym));
    }
}
