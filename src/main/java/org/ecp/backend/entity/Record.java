package org.ecp.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime time;
    private Double consume;
    private Double normal;
    private Double low;
    private Double high;
    @ManyToOne()
    @JoinColumn(name = "contract_id")
    private Contract contract;
}