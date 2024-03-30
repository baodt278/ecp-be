package org.ecp.backend.repository;

import org.ecp.backend.dto.BillDto;
import org.ecp.backend.entity.Bill;
import org.ecp.backend.enums.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    @Query("SELECT new org.ecp.backend.dto.BillDto(b.code, b.createdAt, b.start, b.end, b.expire, b.consume, b.normal, b.low, b.high, b.cost, b.tax, b.charge, b.total, b.status, b.contract.name) from Bill b " +
            "WHERE b.contract.name = :contractName")
    List<BillDto> findByContractName(String contractName);

    @Query("SELECT b FROM Bill b WHERE b.status = :status AND b.expire <= :date")
    List<Bill> findBillsByStatusAndExpireDate(BillStatus status, Date date);

    @Query("SELECT b FROM Bill b WHERE b.contract.name = :name AND b.end = :date ORDER BY b.end ASC")
    Bill findBillsByContractNameAndEndDate(String name, Date date);

    @Query("SELECT new org.ecp.backend.dto.BillDto(b.code, b.createdAt, b.start, b.end, b.expire, b.consume, b.normal, b.low, b.high, b.cost, b.tax, b.charge, b.total, b.status, b.contract.name) from Bill b " +
            "WHERE b.contract.company.acronym = :acronym " +
            "AND (:status IS NULL OR b.status = :status) " +
            "AND b.end = :endDate")
    List<BillDto> findByCompanyAndStatusAndEndDate(String acronym, BillStatus status, Date endDate);

    Optional<Bill> findByCode(String code);
}
