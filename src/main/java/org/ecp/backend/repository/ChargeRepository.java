package org.ecp.backend.repository;

import org.ecp.backend.entity.Charge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ChargeRepository extends JpaRepository<Charge, Long> {
    List<Charge> findByContractNameAndCreatedAtBetween(String contractName, Date from, Date to);
}
