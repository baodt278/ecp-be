package org.ecp.backend.service;

import org.ecp.backend.dto.RecordDto;
import org.ecp.backend.dto.request.ValueDto;
import org.ecp.backend.dto.response.ServerResponseDto;

public interface RecordService {
    ServerResponseDto createRecord(String contractName, String date, ValueDto dto);

    ServerResponseDto predictValueCurrentMonth(String contractName, String date);

    ServerResponseDto findRecordsCurrentMonth(String contractName, String date);

    ServerResponseDto findConsumeTime7DaysBefore(String contractName, String date);

    ServerResponseDto findConsume6MonthsBefore(String contractName, String date);
}
