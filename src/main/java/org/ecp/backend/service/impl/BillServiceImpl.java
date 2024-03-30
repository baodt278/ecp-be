package org.ecp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.constant.CommonConstant;
import org.ecp.backend.dto.BillDto;
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
import org.ecp.backend.service.BillService;
import org.ecp.backend.utils.CalculatorUtils;
import org.ecp.backend.utils.DateUtils;
import org.ecp.backend.utils.GenerateUtils;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.ecp.backend.utils.CalculatorUtils.calculateNormal;
import static org.ecp.backend.utils.CalculatorUtils.getPrices;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {
    private final RecordRepository recordRepo;
    private final ChargeRepository chargeRepo;
    private final ContractRepository contractRepo;
    private final PriceRepository priceRepo;
    private final BaseRepository baseRepo;
    private final BillRepository billRepo;

    @Override
    public ServerResponseDto getBillsContract(String contractName) {
        return new ServerResponseDto(CommonConstant.SUCCESS, billRepo.findByContractName(contractName));
    }

    @Override
    public ServerResponseDto payBill(String code) {
        Bill bill = billRepo.findByCode(code)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Hoa don nay khong ton tai"));
        bill.setStatus(BillStatus.PAID);
        billRepo.save(bill);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Da thanh toan thanh cong");
    }
    @Override
    public ServerResponseDto getBillsCompany(String acronym, Date date) {
        Date endOfMonth = DateUtils.getEndOfMonth(date);
        List<BillDto> dtos = billRepo.findByCompanyAndStatusAndEndDate(acronym, null, endOfMonth);
        return new ServerResponseDto(CommonConstant.SUCCESS, dtos);
    }

    @Override
    public ServerResponseDto totalAnalyst(String acronym, Date date) {
        Date endOfMonth = DateUtils.getEndOfMonth(date);
        List<BillDto> dtos = billRepo.findByCompanyAndStatusAndEndDate(acronym, BillStatus.PAID, endOfMonth);
        double total = dtos.stream().mapToDouble(BillDto::getTotal).sum();
        return new ServerResponseDto(CommonConstant.SUCCESS, total);
    }

    @Override
    public ServerResponseDto manualCreate(String contractName, Date date) {
        Date startOfMonth = DateUtils.getStartOfMonth(date);
        Date endOfMonth = DateUtils.getEndOfMonth(date);
        int expire_after_days = Integer.parseInt(baseRepo.getValue(CommonConstant.EXPIRED_AFTER_DAYS));
        Date expireDate = DateUtils.add(endOfMonth, Calendar.DATE, expire_after_days);
        Contract contract = contractRepo.findByName(contractName)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Khong ton tai hop dong"));
        ContractType type = contract.getType();
        Volt volt = contract.getVolt();
        List<Double> values = priceRepo.findValueByTypeAndVolt(type, volt);
        double[] prices = getPrices(values);
        List<Record> records = recordRepo.findByContractNameAndTimeRange(contractName, startOfMonth, endOfMonth);
        int size = records.size();
        double consume = records.stream().mapToDouble(Record::getConsume).sum();
        double normal = records.stream().mapToDouble(Record::getNormal).sum();
        double low = records.stream().mapToDouble(Record::getLow).sum();
        double high = records.stream().mapToDouble(Record::getHigh).sum();
        double cost = switch (type) {
            case FAMILY -> CalculatorUtils.calFamily(contract.getHouses(), consume, prices);
            case EDU_MEDIC, PUBLIC_GOV -> {
                double normal1 = calculateNormal(records, size);
                yield CalculatorUtils.calComplexPublicGovEduMedic(normal1, prices[0]);
            }
            case PRODUCE, BUSINESS -> CalculatorUtils.calProduceBusiness(normal, low, high, prices);
            default -> 0;
        };

        List<Charge> charges = chargeRepo.findByContractNameAndCreatedAtBetween(contractName, startOfMonth, endOfMonth);
        int tax_percent = Integer.parseInt(baseRepo.getValue(CommonConstant.TAX_PERCENT));
        double tax = cost * tax_percent / 100;
        double charge = charges.stream().mapToDouble(Charge::getValue).sum();
        double total = cost + tax + charge;
        Bill bill = Bill.builder()
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
        billRepo.save(bill);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Tao hoa don thanh cong");
    }
}
