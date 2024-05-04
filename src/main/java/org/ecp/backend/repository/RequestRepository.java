package org.ecp.backend.repository;

import org.ecp.backend.entity.Request;
import org.ecp.backend.enums.RequestStatus;
import org.ecp.backend.enums.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query("SELECT r FROM Request r WHERE r.type = :type AND r.status = :status")
    List<Request> findRequestsBy(RequestType type, RequestStatus status);

    @Query("SELECT r FROM Request r WHERE r.client.username = :username AND r.type = 'CLIENT_VERIFY'")
    List<Request> findVerifyRequestsByClient(String username);

    @Query("SELECT r FROM Request r WHERE r.company.acronym = :acronym AND r.type NOT IN (:types) AND r.status = :status")
    List<Request> findRequestsCompany(String acronym, List<RequestType> types, RequestStatus status);

    @Query("SELECT r FROM Request r WHERE r.client.username = :username AND r.type != 'CLIENT_VERIFY' AND r.status = 'PENDING'")
    List<Request> findRequestsForClient(String username);

    @Query("SELECT r FROM Request r WHERE r.client.username = :username AND r.type != 'CLIENT_VERIFY' AND r.status != 'PENDING'")
    List<Request> findRequestsForClient1(String username);

    Optional<Request> findRequestByCode(String code);

    @Query("SELECT r FROM Request r WHERE r.company.acronym = :acronym AND r.status != 'PENDING'")
    List<Request> findRequestsByCompanyAcronym(String acronym);

    @Query("SELECT COUNT(1) FROM Request r WHERE r.company.acronym = :acronym")
    int countRequests(String acronym);

    @Query("SELECT COUNT(1) FROM Request r WHERE r.company.acronym = :acronym " +
            "AND (r.status = 'APRROVED' OR r.status = 'REJECTED')" +
            "AND ((MONTH(r.reviewedAt)) = MONTH(:date) OR (MONTH(r.acceptedAt)) = MONTH(:date))")
    Integer countRequestsDone(String acronym, Date date);
}
