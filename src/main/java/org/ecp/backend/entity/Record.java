package org.ecp.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date time;
    private double consume;
    private double normal;
    private double low;
    private double high;
    @ManyToOne()
    @JoinColumn(name = "contract_id")
    private Contract contract;
}