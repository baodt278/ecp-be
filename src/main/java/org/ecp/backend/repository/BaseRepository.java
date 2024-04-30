package org.ecp.backend.repository;

import org.ecp.backend.entity.Base;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BaseRepository extends JpaRepository<Base, Long> {
    boolean existsByObject(String object);

    Optional<Base> findByObject(String object);
    @Query("SELECT b.value FROM Base b WHERE b.object = :object ")
    String getValue(String object);
}
