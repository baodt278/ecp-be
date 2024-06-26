package org.ecp.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.ecp.backend.enums.BillStatus;

import java.util.Date;

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
    private Date createdAt;

    private Date start;
    private Date end;
    private Date expire;

    private double consume;
    private double normal;
    private double low;
    private double high;

    private double cost;
    private double tax;
    private double charge;
    private double total;

    @Enumerated(EnumType.STRING)
    private BillStatus status;
    @ManyToOne()
    @JoinColumn(name = "contract_id")
    private Contract contract;
}
