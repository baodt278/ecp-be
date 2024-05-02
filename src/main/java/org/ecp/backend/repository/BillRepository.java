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
    @Query("SELECT new org.ecp.backend.dto.BillDto(b.code, b.createdAt, b.start, b.end, b.expire, b.consume, b.normal, b.low, b.high, b.cost, b.tax, b.charge, b.total, b.status) from Bill b " +
            "WHERE b.contract.name = :contractName")
    List<BillDto> findByContractName(String contractName);

    @Query("SELECT b FROM Bill b WHERE b.status = :status AND b.expire <= :date")
    List<Bill> findBillsByStatusAndExpireDate(BillStatus status, Date date);

    @Query("SELECT b FROM Bill b WHERE b.contract.name = :name AND b.end = :date ORDER BY b.end ASC")
    Bill findBillsByContractNameAndEndDate(String name, Date date);

    @Query("SELECT new org.ecp.backend.dto.BillDto(b.code, b.createdAt, b.start, b.end, b.expire, b.consume, b.normal, b.low, b.high, b.cost, b.tax, b.charge, b.total, b.status) from Bill b " +
            "WHERE b.contract.company.acronym = :acronym " +
            "AND (:status IS NULL OR b.status = :status) " +
            "AND MONTH(b.end) = MONTH(:endDate)")
    List<BillDto> findByCompanyAndStatusAndEndDate(String acronym, BillStatus status, Date endDate);

    @Query("SELECT new org.ecp.backend.dto.BillDto(b.code, b.createdAt, b.start, b.end, b.expire, b.consume, b.normal, b.low, b.high, b.cost, b.tax, b.charge, b.total, b.status) from Bill b " +
            "WHERE b.contract.company.acronym = :acronym " +
            "AND (b.status != :status) " +
            "AND MONTH(b.end) = MONTH(:endDate)")
    List<BillDto> findByCompanyNotPaid(String acronym, BillStatus status, Date endDate);

    @Query("SELECT SUM(b.total) from Bill b " +
            "WHERE b.contract.company.acronym = :acronym " +
            "AND (:status IS NULL OR b.status = :status) " +
            "AND MONTH(b.end) = MONTH(:endDate)")
    Double countByCompanyAndStatusAndEndDate(String acronym, BillStatus status, Date endDate);

    @Query("SELECT new org.ecp.backend.dto.BillDto(b.code, b.createdAt, b.start, b.end, b.expire, b.consume, b.normal, b.low, b.high, b.cost, b.tax, b.charge, b.total, b.status) from Bill b " +
            "WHERE b.contract.client.username = :username " +
            "AND MONTH(b.end) = MONTH(:endDate)")
    List<BillDto> findByUsernameAndEndDate(String username, Date endDate);

    @Query("SELECT b FROM Bill b WHERE MONTH(b.end) = MONTH(:date) AND b.contract.name = :contractName")
    Optional<Bill> findByEnd(String contractName, Date date);

    @Query("SELECT new org.ecp.backend.dto.BillDto(b.code, b.createdAt, b.start, b.end, b.expire, b.consume, b.normal, b.low, b.high, b.cost, b.tax, b.charge, b.total, b.status) from Bill b " +
            "WHERE b.contract.client.username = :username " +
            "AND b.contract.company.acronym = :acronym")
    List<BillDto> findByUsernameAndCompany(String username, String acronym);

    Optional<Bill> findByCode(String code);

    boolean existsByEnd(Date date);
}
