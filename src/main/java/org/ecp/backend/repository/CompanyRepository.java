package org.ecp.backend.repository;

import org.ecp.backend.dto.response.CompanyResponse;
import org.ecp.backend.dto.response.ResponseDto;
import org.ecp.backend.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByAcronym(String acronym);

    Optional<Company> findByAcronym(String acronym);

    @Query("SELECT new org.ecp.backend.dto.response.CompanyResponse(c.name, c.acronym, c.address) FROM Company c")
    List<CompanyResponse> findCompanies();

    @Query("SELECT new org.ecp.backend.dto.response.CompanyResponse(c.name, c.acronym, c.address) FROM Company c " +
            "WHERE c.acronym = :acronym")
    Optional<CompanyResponse> findCompanyByAcronym(String acronym);
}
