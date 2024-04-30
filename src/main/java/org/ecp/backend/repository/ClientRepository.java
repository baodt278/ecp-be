package org.ecp.backend.repository;

import org.ecp.backend.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPersonId(String personId);

    Optional<Client> findByEmail(String email);

    Optional<Client> findByUsername(String username);
}
