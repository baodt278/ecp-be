package org.ecp.backend.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AcceptRequest {
    private ActionDto action;
    private List<ChargeDto> charges;
}
