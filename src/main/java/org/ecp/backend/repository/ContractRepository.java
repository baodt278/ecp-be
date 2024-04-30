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

    @Query("SELECT new org.ecp.backend.dto.ContractDto(c.name, c.address, c.houses, c.createdAt, c.updatedAt, c.type, c.status, c.volt, c.company.acronym, c.company.name) FROM Contract c WHERE c.client.username = :username AND c.status != 'CANCELED'")
    List<ContractDto> findContractClientOwner(String username);

    @Query("SELECT new org.ecp.backend.dto.ContractDto(c.name, c.address, c.houses, c.createdAt, c.updatedAt, c.type, c.status, c.volt, c.company.acronym, c.client.username) FROM Contract c WHERE c.company.acronym = :acronym ORDER BY c.status ASC, c.createdAt DESC")
    List<ContractDto> findContractCompanySign(String acronym);

    @Query("SELECT COUNT(1) FROM Contract c WHERE c.company.acronym = :acronym")
    int countContracts(String acronym);

    @Query("SELECT COUNT(1) FROM Contract c WHERE c.company.acronym = :acronym AND c.status = 'ACTIVE'")
    int countContractsActive(String acronym);

    @Query("SELECT DISTINCT COUNT(c.client.username) FROM Contract c WHERE c.company.acronym = :acronym")
    int countClients(String acronym);

    @Query("SELECT DISTINCT c.company.acronym FROM Contract c WHERE c.client.username = :username AND c.status = 'ACTIVE'")
    List<String> findCompanyAcronymByClient(String username);
}
