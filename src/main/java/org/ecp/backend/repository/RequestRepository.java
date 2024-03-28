package org.ecp.backend.repository;

import org.ecp.backend.entity.Request;
import org.ecp.backend.enums.RequestStatus;
import org.ecp.backend.enums.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query("SELECT r FROM Request r WHERE r.type = :type AND r.status = :status")
    List<Request> findRequestsBy(RequestType type, RequestStatus status);

    @Query("SELECT r FROM Request r WHERE r.company.acronym = :acronym AND r.type NOT IN (:types) AND r.status = :status")
    List<Request> findRequestsCompany(String acronym, List<RequestType> types, RequestStatus status);

    List<Request> findRequestsByClient_Username(String username);

    Optional<Request> findRequestByCode(String code);
}
