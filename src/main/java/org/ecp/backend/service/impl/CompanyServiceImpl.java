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
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Công ty đã tồn tại!");
        Company company = Company.builder()
                .name(dto.getName())
                .acronym(acronym)
                .address(dto.getAddress())
                .build();
        companyRepo.save(company);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Tạo công ty thành công!");
    }

    @Override
    public ServerResponseDto update(CompanyDto dto) {
        Company company = companyRepo.findByAcronym(dto.getAcronym()).orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Công ty không tồn tại!"));
        company.setName(dto.getName());
        company.setAddress(dto.getAddress());
        companyRepo.save(company);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Cập nhật công ty thành công!");
    }

    @Override
    public ServerResponseDto getCompanies() {
        return new ServerResponseDto(CommonConstant.SUCCESS, companyRepo.findCompanies());
    }

    @Override
    public ServerResponseDto getCompany(String acronym){
        if (!companyRepo.existsByAcronym(acronym))
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Công ty không tồn tại!");
        return new ServerResponseDto(CommonConstant.SUCCESS, companyRepo.findByAcronym(acronym));
    }

    @Override
    public ServerResponseDto delete(String acronym){
        Company company = companyRepo.findByAcronym(acronym).orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Công ty không tồn tại!"));
        companyRepo.delete(company);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Xóa công ty thành công!");
    }
}
