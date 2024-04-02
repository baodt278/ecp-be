package org.ecp.backend.repository;

import org.ecp.backend.dto.RecordDto;
import org.ecp.backend.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    @Query("SELECT r FROM Record r " +
            "WHERE r.contract.name = :contractName " +
            "AND (r.time BETWEEN :from AND :to) " +
            "ORDER BY r.time ASC")
    List<Record> findByContractNameAndTimeRange(String contractName, Date from, Date to);

    @Query("SELECT new org.ecp.backend.dto.RecordDto(r.time, r.consume, r.normal, r.low, r.high) FROM Record r " +
            "WHERE r.contract.name = :contractName " +
            "AND (r.time BETWEEN :from AND :to) " +
            "ORDER BY r.time DESC")
    List<RecordDto> findByContractNameOnTimeRange(String contractName, Date from, Date to);
    @Query("SELECT r FROM Record r WHERE r.time = DATE(:time)")
    Record findByStringTime(String time);
}
