package org.ecp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.constant.CommonConstant;
import org.ecp.backend.dto.RecordDto;
import org.ecp.backend.dto.response.PredictValueDto;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.entity.Bill;
import org.ecp.backend.entity.Contract;
import org.ecp.backend.entity.Record;
import org.ecp.backend.enums.ContractType;
import org.ecp.backend.enums.Volt;
import org.ecp.backend.exception.ApplicationRuntimeException;
import org.ecp.backend.repository.BillRepository;
import org.ecp.backend.repository.ContractRepository;
import org.ecp.backend.repository.PriceRepository;
import org.ecp.backend.repository.RecordRepository;
import org.ecp.backend.service.RecordService;
import org.ecp.backend.utils.CalculatorUtils;
import org.ecp.backend.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.ecp.backend.utils.CalculatorUtils.*;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {
    private final RecordRepository recordRepo;
    private final ContractRepository contractRepo;
    private final PriceRepository priceRepo;
    private final BillRepository billRepo;

    @Override
    public ServerResponseDto createRecord(String contractName, RecordDto dto) {
        Contract contract = contractRepo.findByName(contractName)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tồn tại hợp đồng!"));
        Record recordCheck = recordRepo.findByStringTime(DateUtils.convertDateToString(dto.getTime(), "yyyy-MM-dd"));
        if (recordCheck != null)
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Đã có bản ghi tồn tại!");
        Record record = Record.builder()
                .time(dto.getTime())
                .consume(dto.getConsume())
                .normal(dto.getNormal())
                .low(dto.getLow())
                .high(dto.getHigh())
                .contract(contract)
                .build();
        recordRepo.save(record);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Tao ban ghi thanh cong");
    }

    @Override
    public ServerResponseDto predictValueCurrentMonth(String contractName) {
        Date now = new Date();
        Date startOfMonth = DateUtils.getStartOfMonth(now);
        Contract contract = contractRepo.findByName(contractName)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tồn tại hợp đồng!"));
        List<Record> records = recordRepo.findByContractNameAndTimeRange(contractName, startOfMonth, now);
        int size = records.size();
        if (size == 0) return new ServerResponseDto(CommonConstant.SUCCESS, new PredictValueDto(0, 0, 0, 0, 0));
        PredictValueDto dto = calculatePredictValue(contract, records);
        return new ServerResponseDto(CommonConstant.SUCCESS, dto);
    }

    private PredictValueDto calculatePredictValue(Contract contract, List<Record> records) {
        int size = records.size();
        ContractType type = contract.getType();
        Volt volt = contract.getVolt();
        List<Double> values = priceRepo.findValueByTypeAndVolt(type, volt);
        double[] prices = getPrices(values);
        double cost;
        switch (type) {
            case FAMILY:
                double consume = calculateConsume(records, size);
                cost = CalculatorUtils.calFamily(contract.getHouses(), consume, prices);
                return new PredictValueDto(consume, 0, 0, 0, cost);
            case EDU_MEDIC:
            case PUBLIC_GOV:
                double normal1 = calculateNormal(records, size);
                cost = CalculatorUtils.calComplexPublicGovEduMedic(normal1, prices[0]);
                return new PredictValueDto(0, normal1, 0, 0, cost);
            case PRODUCE:
            case BUSINESS:
                double normal = calculateNormal(records, size);
                double low = calculateLow(records, size);
                double high = calculateHigh(records, size);
                cost = CalculatorUtils.calProduceBusiness(normal, low, high, prices);
                return new PredictValueDto(0, normal, low, high, cost);
            default:
                return null;
        }
    }

    @Override
    public ServerResponseDto findRecordsCurrentMonth(String contractName) {
        Date now = new Date();
        Date startOfMonth = DateUtils.getStartOfMonth(now);
        List<RecordDto> records = recordRepo.findByContractNameOnTimeRange(contractName, startOfMonth, now);
        return new ServerResponseDto(CommonConstant.SUCCESS, records);
    }

    @Override
    public ServerResponseDto findConsumeTime7DaysBefore(String contractName) {
        List<RecordDto> dtos = new ArrayList<>();
        Date yesterday = DateUtils.getEndOfDay(DateUtils.add(new Date(), Calendar.DATE, -1));
        Date day7Before = DateUtils.getStartOfDay(DateUtils.add(yesterday, Calendar.DATE, -6));
        String day8Before = DateUtils.convertDateToString(DateUtils.add(yesterday, Calendar.DATE, -7), "yyyy-MM-dd");
        Record record8Before = recordRepo.findByStringTime(day8Before);
        List<Record> records = recordRepo.findByContractNameAndTimeRange(contractName, day7Before, yesterday);

        for (int i = 0; i < records.size(); i++) {
            Record currentRecord = records.get(i);
            Record previousRecord = i == 0 ? record8Before : records.get(i - 1);
            RecordDto dto = createRecordDto(currentRecord, previousRecord);
            dtos.add(dto);
        }
        dtos.removeAll(Collections.singleton(null));
        return new ServerResponseDto(CommonConstant.SUCCESS, dtos);
    }

    private RecordDto createRecordDto(Record currentRecord, Record previousRecord) {
        if (previousRecord == null) {
            return new RecordDto(currentRecord.getTime(), 0, 0, 0, 0);
        } else {
            return new RecordDto(currentRecord.getTime(),
                    CalculatorUtils.roundToTwoDecimalPlaces(currentRecord.getConsume() - previousRecord.getConsume()),
                    CalculatorUtils.roundToTwoDecimalPlaces(currentRecord.getNormal() - previousRecord.getNormal()),
                    CalculatorUtils.roundToTwoDecimalPlaces(currentRecord.getLow() - previousRecord.getLow()),
                    CalculatorUtils.roundToTwoDecimalPlaces(currentRecord.getHigh() - previousRecord.getHigh()));
        }
    }

    @Override
    public ServerResponseDto findConsume6MonthsBefore(String contractName) {
        List<RecordDto> dtos = IntStream.rangeClosed(1, 6)
                .mapToObj(i -> findTotalPerMonth(contractName, new Date(), -i))
                .collect(Collectors.toList());
        dtos.removeAll(Collections.singleton(null));
        return new ServerResponseDto(CommonConstant.SUCCESS, dtos);
    }

    public RecordDto findTotalPerMonth(String contractName, Date date, int minusMonth) {
        Date before = DateUtils.add(date, Calendar.MONTH, minusMonth);
        Date endOfMonth = DateUtils.getEndOfMonth(before);
        Bill bill = billRepo.findBillsByContractNameAndEndDate(contractName, endOfMonth);
        if (bill == null) return null;
        return RecordDto.builder()
                .time(before)
                .consume(bill.getConsume())
                .normal(bill.getNormal())
                .low(bill.getLow())
                .high(bill.getHigh())
                .build();
    }
}
