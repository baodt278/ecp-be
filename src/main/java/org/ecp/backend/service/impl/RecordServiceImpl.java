package org.ecp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.constant.CommonConstant;
import org.ecp.backend.dto.RecordDto;
import org.ecp.backend.dto.request.ValueDto;
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

import java.time.Month;
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
    public ServerResponseDto createRecord(String contractName, String date, ValueDto dto) {
        Date dateTime = DateUtils.convertStringToDate(date, "yyyy-MM-dd");
        Contract contract = contractRepo.findByName(contractName)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tồn tại hợp đồng!"));
        Record recordCheck = recordRepo.findByDateAndContract(dateTime, contractName);
        if (recordCheck != null)
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Đã có bản ghi tồn tại!");
        Record record = Record.builder()
                .time(dateTime)
                .consume(dto.getConsume())
                .normal(dto.getNormal())
                .low(dto.getLow())
                .high(dto.getHigh())
                .contract(contract)
                .build();
        recordRepo.save(record);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Tạo bản ghi thành công!");
    }

    @Override
    public ServerResponseDto predictValueCurrentMonth(String contractName, String date) {
        Date dateTime = DateUtils.convertStringToDate(date, "yyyy-MM-dd");
        Date startOfMonth = DateUtils.getStartOfMonth(dateTime);
        Date endOfMonth = DateUtils.getEndOfMonth(dateTime);
        Contract contract = contractRepo.findByName(contractName)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tồn tại hợp đồng!"));
        List<Record> records = recordRepo.findByContractNameAndTimeRange(contractName, startOfMonth, endOfMonth);
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
    public ServerResponseDto findRecordsCurrentMonth(String contractName, String date) {
        Date dateTime = DateUtils.convertStringToDate(date, "yyyy-MM-dd");
        Date startOfMonth = DateUtils.getStartOfMonth(dateTime);
        Date endOfMonth = DateUtils.getEndOfMonth(dateTime);
        List<RecordDto> records = recordRepo.findByContractNameOnTimeRange(contractName, startOfMonth, endOfMonth);
        return new ServerResponseDto(CommonConstant.SUCCESS, records);
    }

    @Override
    public ServerResponseDto findConsumeTime7DaysBefore(String contractName, String date) {
        List<RecordDto> dtos = new ArrayList<>();
        Date dateTime = DateUtils.convertStringToDate(date, "yyyy-MM-dd");
        if (compareMonthAndYear(dateTime, new Date())) dateTime = new Date();
        Date yesterday = DateUtils.getEndOfDay(DateUtils.add(dateTime, Calendar.DATE, -1));
        Date day7Before = DateUtils.getStartOfDay(DateUtils.add(yesterday, Calendar.DATE, -6));
        Date day8Before = DateUtils.getStartOfDay(DateUtils.add(yesterday, Calendar.DATE, -7));
        Record record8Before = recordRepo.findByDateAndContract(day8Before, contractName);
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
    public ServerResponseDto findConsume6MonthsBefore(String contractName, String date) {
        Date dateTime = DateUtils.convertStringToDate(date, "yyyy-MM-dd");
        if (compareMonthAndYear(dateTime, new Date())) {
            dateTime = new Date();
        }
        Date finalDateTime = dateTime;
        List<RecordDto> dtos = IntStream.rangeClosed(1, 6)
                .mapToObj(i -> findTotalPerMonth(contractName, finalDateTime, -i))
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
                .time(endOfMonth)
                .consume(bill.getConsume())
                .normal(bill.getNormal())
                .low(bill.getLow())
                .high(bill.getHigh())
                .build();
    }

    private static boolean compareMonthAndYear(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }
}
