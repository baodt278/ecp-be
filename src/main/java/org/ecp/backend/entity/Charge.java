package org.ecp.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ecp.backend.enums.ChargeType;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Charge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contractName;
    private String requestCode;
    @Enumerated(EnumType.STRING)
    private ChargeType type;
    @Column(columnDefinition = "text")
    private String reason;
    private Double value;
    @ManyToOne()
    @JoinColumn(name = "company_id")
    private Company company;
}
