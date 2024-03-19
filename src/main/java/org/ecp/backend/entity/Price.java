package org.ecp.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.ecp.backend.enums.ContractType;
import org.ecp.backend.enums.PriceTag;
import org.ecp.backend.enums.Volt;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private ContractType contractType;
    @Enumerated(EnumType.STRING)
    private Volt volt;
    @Enumerated(EnumType.STRING)
    private PriceTag tag;
    private Double price;
}
