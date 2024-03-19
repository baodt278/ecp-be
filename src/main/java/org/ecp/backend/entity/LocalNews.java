package org.ecp.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.ecp.backend.enums.NewsStatus;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LocalNews extends News {
    private String reviewedBy;
    @Enumerated(EnumType.STRING)
    private NewsStatus status;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
}
