package org.ecp.backend.repository;

import org.ecp.backend.dto.ContractDto;
import org.ecp.backend.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByName(String name);
    @Query("SELECT new org.ecp.backend.dto.ContractDto(c.name, c.address, c.houses, c.createdAt, c.type, c.status, c.volt, c.company.acronym, c.company.name) FROM Contract c WHERE c.client.username = :username")
    List<ContractDto> findContractClientOwner(String username);
    @Query("SELECT new org.ecp.backend.dto.ContractDto(c.name, c.address, c.houses, c.createdAt, c.type, c.status, c.volt, c.company.acronym, c.client.username) FROM Contract c WHERE c.company.acronym = :acronym")
    List<ContractDto> findContractCompanySign(String acronym);
}
