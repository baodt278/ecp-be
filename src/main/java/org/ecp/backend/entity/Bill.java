package org.ecp.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.ecp.backend.enums.BillStatus;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate expiredDate;

    private Double consume;
    private Double normal;
    private Double low;
    private Double high;

    private Double cost;
    private Double tax;
    private Double plus;
    private Double minus;
    private Double total;

    @Enumerated(EnumType.STRING)
    private BillStatus status;
    @ManyToOne()
    @JoinColumn(name = "contract_id")
    private Contract contract;
}
