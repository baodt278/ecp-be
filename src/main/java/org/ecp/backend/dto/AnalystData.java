package org.ecp.backend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AnalystData {
    List<TotalDto> values;
    List<BillDto> bills;
    double total;
    double percentTotal;
    double paid;
    double percentPaid;
    int contracts;
    int requests;
    double percentRequests;
}
