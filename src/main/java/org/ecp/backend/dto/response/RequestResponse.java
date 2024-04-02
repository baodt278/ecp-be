package org.ecp.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
    private List<String> imageUrls;
    private RequestType type;
    private RequestStatus status;
    private String info;
    private String reviewedBy;
    private String reviewText;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewedAt;
    private String acceptedBy;
    private String acceptText;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date acceptedAt;
    private String companyAcronym;
    private String usernameClient;
}
