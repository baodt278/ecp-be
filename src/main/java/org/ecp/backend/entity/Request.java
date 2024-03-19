package org.ecp.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.ecp.backend.enums.RequestStatus;
import org.ecp.backend.enums.RequestType;

import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;
    @Column(columnDefinition = "text")
    private String images;
    @Enumerated(EnumType.STRING)
    private RequestType type;
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
    @Column(columnDefinition = "text")
    private String infoContract;

    private String reviewedBy;
    @Column(columnDefinition = "text")
    private String reviewText;
    private LocalDateTime reviewedAt;

    private String acceptedBy;
    @Column(columnDefinition = "text")
    private String acceptText;
    private LocalDateTime acceptedAt;

    @ManyToOne()
    @JoinColumn(name = "company_id")
    private Company company;
    @ManyToOne()
    @JoinColumn(name = "consumer_id")
    private Consumer consumer;
}
