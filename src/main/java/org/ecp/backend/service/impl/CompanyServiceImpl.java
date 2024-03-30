package org.ecp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.constant.CommonConstant;
import org.ecp.backend.dto.CompanyDto;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.entity.Company;
import org.ecp.backend.exception.ApplicationRuntimeException;
import org.ecp.backend.repository.CompanyRepository;
import org.ecp.backend.service.CompanyService;
import org.ecp.backend.utils.GenerateUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepo;

    @Override
    public ServerResponseDto create(CompanyDto dto) {
        String acronym = GenerateUtils.generateAcronym(dto.getAcronym());
        if (companyRepo.existsByAcronym(acronym))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Cong ty da ton tai");
        Company company = Company.builder()
                .name(dto.getName())
                .acronym(acronym)
                .address(dto.getAddress())
                .build();
        companyRepo.save(company);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Tao cong ty thanh cong");
    }

    @Override
    public ServerResponseDto getCompanies() {
        return new ServerResponseDto(CommonConstant.SUCCESS, companyRepo.findCompanies());
    }

    @Override
    public ServerResponseDto getCompany(String acronym){
        if (!companyRepo.existsByAcronym(acronym))
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Cong ty khong ton tai");
        return new ServerResponseDto(CommonConstant.SUCCESS, companyRepo.findByAcronym(acronym));
    }
}
