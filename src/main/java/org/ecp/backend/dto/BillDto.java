package org.ecp.backend.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecp.backend.entity.Contract;
import org.ecp.backend.enums.BillStatus;

import java.util.Date;

@Data
@AllArgsConstructor
public class BillDto {
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

    private BillStatus status;
    private String contractName;
}
