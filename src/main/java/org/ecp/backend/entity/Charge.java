package org.ecp.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.ecp.backend.enums.ChargeType;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Charge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contractName;
    private String requestCode;
    private Date createdAt;
    @Enumerated(EnumType.STRING)
    private ChargeType type;
    @Column(columnDefinition = "text")
    private String reason;
    private double value;
    @ManyToOne()
    @JoinColumn(name = "company_id")
    private Company company;
}
