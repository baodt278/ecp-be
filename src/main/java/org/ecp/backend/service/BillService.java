package org.ecp.backend.service;

import org.ecp.backend.dto.response.ServerResponseDto;

import java.util.Date;

public interface BillService {
    ServerResponseDto getBillsContract(String contractName);

    ServerResponseDto getBillCurrentMonth(String username);

    ServerResponseDto payBill(String code);

    ServerResponseDto getBillsCompany(String acronym, Date date);

    ServerResponseDto totalAnalyst(String acronym, Date date);

    ServerResponseDto manualCreate(String contractName, Date date);
}
