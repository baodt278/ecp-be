package org.ecp.backend.scheduler;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.constant.CommonConstant;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.entity.Bill;
import org.ecp.backend.entity.Charge;
import org.ecp.backend.entity.Contract;
import org.ecp.backend.entity.Record;
import org.ecp.backend.enums.BillStatus;
import org.ecp.backend.enums.ContractType;
import org.ecp.backend.enums.Volt;
import org.ecp.backend.exception.ApplicationRuntimeException;
import org.ecp.backend.repository.*;
import org.ecp.backend.utils.CalculatorUtils;
import org.ecp.backend.utils.DateUtils;
import org.ecp.backend.utils.GenerateUtils;
import org.ecp.backend.utils.TextUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.ecp.backend.utils.CalculatorUtils.*;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final RecordRepository recordRepo;
    private final ChargeRepository chargeRepo;
    private final ContractRepository contractRepo;
    private final PriceRepository priceRepo;
    private final BaseRepository baseRepo;
    private final BillRepository billRepo;
    @Value("${file_path}")
    private String filePath;

    //    @Scheduled(cron = "0 0 6 1 * ?")
    public ServerResponseDto createBill() {
        List<Bill> bills = contractRepo.findAll().stream()
                .map(this::createBillLastMonth)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        billRepo.saveAll(bills);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Tạo hóa đơn hàng tháng thành công!");
    }

    //    @Scheduled(cron = "0 0 6 15 * ?")
    public ServerResponseDto updateExpire() {
        List<Bill> bills = billRepo.findBillsByStatusAndExpireDate(BillStatus.UNPAID, new Date());
        bills.forEach(bill -> bill.setStatus(BillStatus.EXPIRED));
        billRepo.saveAll(bills);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Thay đổi trạng thái hóa đơn hết hạn thành công!");
    }

    //    @Scheduled(cron = "0 0 0 * * ?")
    public ServerResponseDto insertRecords() {
        List<String> names = TextUtils.getTxtFileNames(filePath);
        recordRepo.saveAll(names.stream().map(this::insertRecord).collect(Collectors.toList()));
        return new ServerResponseDto(CommonConstant.SUCCESS, "Thêm bản ghi thành công!");
    }

    private Bill createBillLastMonth(Contract contract) {
        Date date = DateUtils.add(new Date(), Calendar.DATE, -1);
        Date startOfMonth = DateUtils.getStartOfMonth(date);
        Date endOfMonth = DateUtils.getEndOfMonth(date);
        if (billRepo.existsByEnd(endOfMonth))
            return null;
        int expire_after_days = Integer.parseInt(baseRepo.getValue(CommonConstant.EXPIRED_AFTER_DAYS));
        Date expireDate = DateUtils.add(endOfMonth, Calendar.DATE, expire_after_days);
        ContractType type = contract.getType();
        Volt volt = contract.getVolt();
        double[] prices = getPrices(priceRepo.findValueByTypeAndVolt(type, volt));
        List<Record> records = recordRepo.findByContractNameAndTimeRange(contract.getName(), startOfMonth, endOfMonth);
        int size = records.size();
        double consume = size == 0 ? 0.0 : calculateConsume(records, size);
        double normal = size == 0 ? 0.0 : calculateNormal(records, size);
        double low = size == 0 ? 0.0 : calculateLow(records, size);
        double high = size == 0 ? 0.0 : calculateHigh(records, size);
        double cost = calculateCost(type, contract.getHouses(), consume, normal, low, high, prices);
        double tax = cost * Integer.parseInt(baseRepo.getValue(CommonConstant.TAX_PERCENT)) / 100;
        double charge = chargeRepo.findByContractNameAndCreatedAtBetween(contract.getName(), startOfMonth, endOfMonth)
                .stream().mapToDouble(Charge::getValue).sum();
        double total = cost + tax + charge;
        return Bill.builder()
                .code(GenerateUtils.generatedCode())
                .createdAt(new Date())
                .start(startOfMonth)
                .end(endOfMonth)
                .expire(expireDate)
                .consume(consume)
                .normal(normal)
                .low(low)
                .high(high)
                .cost(cost)
                .tax(tax)
                .charge(charge)
                .total(total)
                .status(BillStatus.UNPAID)
                .contract(contract)
                .build();
    }

    private double calculateCost(ContractType type, int houses, double consume, double normal, double low, double high, double[] prices) {
        return switch (type) {
            case FAMILY -> CalculatorUtils.calFamily(houses, consume, prices);
            case EDU_MEDIC, PUBLIC_GOV -> CalculatorUtils.calComplexPublicGovEduMedic(normal, prices[0]);
            case PRODUCE, BUSINESS -> CalculatorUtils.calProduceBusiness(normal, low, high, prices);
            default -> 0;
        };
    }

    private Record insertRecord(String contractName) {
        Contract contract = contractRepo.findByName(contractName).orElse(null);
        String line = readFirstLine(filePath + contractName + ".txt");
        String[] data = line.split(",");
        return Record.builder()
                .consume(Double.parseDouble(data[0]))
                .normal(Double.parseDouble(data[1]))
                .low(Double.parseDouble(data[2]))
                .high(Double.parseDouble(data[3]))
                .time(DateUtils.convertStringToDate(data[4], "yyyy-MM-dd HH:mm:ss"))
                .contract(contract)
                .build();
    }

    private String readFirstLine(String filePath) {
        String firstLine = null;
        Path path = Paths.get(filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            firstLine = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return firstLine;
    }
}
