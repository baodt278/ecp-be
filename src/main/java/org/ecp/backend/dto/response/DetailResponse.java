package org.ecp.backend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DetailResponse {
    private String personId;
    private String personNam;
    private boolean active;
}
