package org.ecp.backend.repository;

import org.ecp.backend.dto.response.EmployeeData;
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

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByUsername(String username);

    @Query("SELECT new org.ecp.backend.dto.response.ResponseDto(e.username, e.role) FROM Employee e " +
            "WHERE e.company.acronym = :acronym ORDER BY e.role DESC, e.username ASC")
    List<ResponseDto> findEmployees(String acronym);

    @Query("SELECT new org.ecp.backend.dto.response.EmployeeData(e.username, e.role, e.fullName, e.email, e.phone, e.address) FROM Employee e " +
            "WHERE e.company.acronym = :acronym ORDER BY e.role DESC, e.username ASC")
    List<EmployeeData> findEmployeeData(String acronym);

    @Query("SELECT COUNT(1) FROM Employee e WHERE e.company.acronym = :acronym")
    int countEmployees(String acronym);
}
