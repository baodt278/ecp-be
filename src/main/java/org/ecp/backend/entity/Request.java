package org.ecp.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.ecp.backend.enums.RequestStatus;
import org.ecp.backend.enums.RequestType;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    @Column(columnDefinition = "text")
    private String description;
    private Date createdAt;
    @Column(columnDefinition = "text")
    private String images;
    @Enumerated(EnumType.STRING)
    private RequestType type;
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
    @Column(columnDefinition = "text")
    private String info;

    private String reviewedBy;
    @Column(columnDefinition = "text")
    private String reviewText;
    private Date reviewedAt;

    private String acceptedBy;
    @Column(columnDefinition = "text")
    private String acceptText;
    private Date acceptedAt;

    @ManyToOne()
    @JoinColumn(name = "company_id")
    private Company company;
    @ManyToOne()
    @JoinColumn(name = "consumer_id")
    private Client client;
}
