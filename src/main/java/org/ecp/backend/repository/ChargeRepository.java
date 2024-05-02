package org.ecp.backend.repository;

import org.ecp.backend.dto.ChargeDto;
import org.ecp.backend.entity.Charge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ChargeRepository extends JpaRepository<Charge, Long> {
    List<Charge> findByContractNameAndCreatedAtBetween(String contractName, Date from, Date to);
    @Query("SELECT new org.ecp.backend.dto.ChargeDto(c.id, c.contractName, c.requestCode, c.createdAt, c.reason, c.value) FROM Charge c WHERE c.contractName = :contractName ORDER BY c.createdAt DESC")
    List<ChargeDto> findByContractName(String contractName);
}
