package org.ecp.backend.dto.response;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecp.backend.entity.Client;
import org.ecp.backend.entity.Company;
import org.ecp.backend.enums.RequestStatus;
import org.ecp.backend.enums.RequestType;

import java.util.Date;

@Data
@NoArgsConstructor
public class RequestResponse {
    private String code;
    private String description;
    private Date createdAt;
    private String imageUrls;
    private RequestType type;
    private RequestStatus status;
    private String info;
    private String reviewedBy;
    private String reviewText;
    private Date reviewedAt;
    private String acceptedBy;
    private String acceptText;
    private Date acceptedAt;
    private String companyAcronym;
    private String usernameClient;
}
