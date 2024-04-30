package org.ecp.backend.service;

import org.ecp.backend.dto.response.ServerResponseDto;

import java.util.Date;

public interface BillService {
    ServerResponseDto getBillsContract(String contractName);

    ServerResponseDto getBillByCode(String username, String acronym);

    ServerResponseDto getBillCurrentMonth(String username, String date);

    ServerResponseDto payBill(String contractName, String date);

    ServerResponseDto getBillsCompany(String acronym, Date date);

    ServerResponseDto totalAnalyst(String acronym, String date);

    ServerResponseDto manualCreate(String contractName, String date);
}
