package org.ecp.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.ecp.backend.enums.ContractStatus;
import org.ecp.backend.enums.ContractType;
import org.ecp.backend.enums.Volt;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private int houses;
    private Date createdAt;

    @Enumerated(EnumType.STRING)
    private ContractType type;
    @Enumerated(EnumType.STRING)
    private ContractStatus status;
    @Enumerated(EnumType.STRING)
    private Volt volt;

    @ManyToOne()
    @JoinColumn(name = "company_id")
    private Company company;
    @ManyToOne()
    @JoinColumn(name = "consumer_id")
    private Client client;
}
