package org.ecp.backend.repository;

import org.ecp.backend.entity.Price;
import org.ecp.backend.enums.ContractType;
import org.ecp.backend.enums.Volt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
    @Query("SELECT p.price FROM Price p WHERE p.contractType = :type " +
            "AND p.volt = :volt " +
            "ORDER BY p.price ASC")
    List<Double> findValueByTypeAndVolt(ContractType type, Volt volt);
    @Query("SELECT p FROM Price p ORDER BY p.contractType ASC, p.volt ASC, p.tag ASC")
    List<Price> findAllPrices();
}
