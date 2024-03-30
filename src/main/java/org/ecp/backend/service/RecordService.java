package org.ecp.backend.service;

import org.ecp.backend.dto.RecordDto;
import org.ecp.backend.dto.response.ServerResponseDto;

public interface RecordService {
    ServerResponseDto createRecord(String contractName, RecordDto dto);

    ServerResponseDto predictValueCurrentMonth(String contractName);

    ServerResponseDto findRecordsCurrentMonth(String contractName);

    ServerResponseDto findConsumeTime7DaysBefore(String contractName);

    ServerResponseDto findConsume6MonthsBefore(String contractName);
}
