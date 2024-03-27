package org.ecp.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.ecp.backend.enums.RequestStatus;
import org.ecp.backend.enums.RequestType;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class RequestResponse {
    private String code;
    private String description;
    private Date createdAt;
    private List<String> imageUrls;
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
