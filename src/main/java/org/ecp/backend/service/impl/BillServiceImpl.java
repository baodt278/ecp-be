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

import static org.ecp.backend.utils.CalculatorUtils.*;

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
    public ServerResponseDto getBillCurrentMonth(String username) {
        Date date = new Date();
        Date endOfMonth = DateUtils.getEndOfMonth(date);
        return new ServerResponseDto(CommonConstant.SUCCESS, billRepo.findByUsernameAndEndDate(username, endOfMonth));
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
        String stringTime = DateUtils.convertDateToString(endOfMonth, "yyyy-MM-dd");
        List<BillDto> dtos = billRepo.findByCompanyAndStatusAndEndDate(acronym, null, stringTime);
        return new ServerResponseDto(CommonConstant.SUCCESS, dtos);
    }

    @Override
    public ServerResponseDto totalAnalyst(String acronym, Date date) {
        Date endOfMonth = DateUtils.getEndOfMonth(date);
        String stringTime = DateUtils.convertDateToString(endOfMonth, "yyyy-MM-dd");
        List<BillDto> dtos = billRepo.findByCompanyAndStatusAndEndDate(acronym, BillStatus.PAID, stringTime);
        double total = dtos.stream().mapToDouble(BillDto::getTotal).sum();
        return new ServerResponseDto(CommonConstant.SUCCESS, total);
    }

    @Override
    public ServerResponseDto manualCreate(String contractName, Date date) {
        Date startOfMonth = DateUtils.getStartOfMonth(date);
        Date endOfMonth = DateUtils.getEndOfMonth(date);
        if (billRepo.existsByEnd(endOfMonth))
            throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Đã có hóa đơn cho tháng này!");
        int expire_after_days = Integer.parseInt(baseRepo.getValue(CommonConstant.EXPIRED_AFTER_DAYS));
        Date expireDate = DateUtils.add(endOfMonth, Calendar.DATE, expire_after_days);
        Contract contract = contractRepo.findByName(contractName)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tồn tại hợp đồng!"));
        ContractType type = contract.getType();
        Volt volt = contract.getVolt();
        List<Double> values = priceRepo.findValueByTypeAndVolt(type, volt);
        double[] prices = getPrices(values);
        List<Record> records = recordRepo.findByContractNameAndTimeRange(contractName, startOfMonth, endOfMonth);
        int size = records.size();
        double consume = size == 0 ? 0.0: calculateConsume(records, size);
        double normal = size == 0 ? 0.0: calculateNormal(records, size);
        double low = size == 0 ? 0.0: calculateLow(records, size);
        double high = size == 0 ? 0.0: calculateHigh(records, size);
        double cost = switch (type) {
            case FAMILY -> CalculatorUtils.calFamily(contract.getHouses(), consume, prices);
            case EDU_MEDIC, PUBLIC_GOV -> CalculatorUtils.calComplexPublicGovEduMedic(normal, prices[0]);
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
