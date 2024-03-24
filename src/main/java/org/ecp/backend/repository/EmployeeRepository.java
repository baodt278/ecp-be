package org.ecp.backend.repository;

import org.ecp.backend.dto.response.ResponseDto;
import org.ecp.backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<Employee> findByUsername(String username);

    @Query("SELECT new org.ecp.backend.dto.response.ResponseDto(e.username, e.role) FROM Employee e " +
            "WHERE e.company.acronym = :acronym")
    List<ResponseDto> findEmployees(String acronym);
}
