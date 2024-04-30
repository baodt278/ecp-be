package org.ecp.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CompanyData {
    private String name;
    private String acronym;
    private String address;
    private int numberEmployees;
    private int numberContracts;
    private int numberClients;
    private int numberRequests;
}
