package org.ecp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.constant.CommonConstant;
import org.ecp.backend.dto.AnalystData;
import org.ecp.backend.dto.BillDto;
import org.ecp.backend.dto.TotalDto;
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
import org.ecp.backend.service.MailService;
import org.ecp.backend.utils.CalculatorUtils;
import org.ecp.backend.utils.DateUtils;
import org.ecp.backend.utils.GenerateUtils;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private final RequestRepository requestRepo;
    private final MailService mailService;

    @Override
    public ServerResponseDto getBillsContract(String contractName) {
        return new ServerResponseDto(CommonConstant.SUCCESS, billRepo.findByContractName(contractName));
    }

    @Override
    public ServerResponseDto getBillByCode(String username, String acronym) {
        List<BillDto> billDto = billRepo.findByUsernameAndCompany(username, acronym);
        return new ServerResponseDto(CommonConstant.SUCCESS, billDto);
    }

    @Override
    public ServerResponseDto getBillCurrentMonth(String username, String date) {
        Date dateTime = DateUtils.convertStringToDate(date, "yyyy-MM-dd");
        Date endOfMonth = DateUtils.getEndOfMonth(dateTime);
        return new ServerResponseDto(CommonConstant.SUCCESS, billRepo.findByUsernameAndEndDate(username, endOfMonth));
    }

    @Override
    public ServerResponseDto payBill(String contractName, String date) {
        try {
            Date dateTime = DateUtils.convertStringToDate(date, "yyyy-MM-dd");
            Bill bill = billRepo.findByEnd(contractName, dateTime).orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Hóa đơn này không tồn tại!"));
            if (bill.getStatus() == BillStatus.PAID)
                throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Hóa đơn này đã được thanh toán!");
            if (bill.getExpire().before(new Date()))
                throw new ApplicationRuntimeException(CommonConstant.BAD_REQUEST, "Hóa đơn này đã quá hạn!");
            bill.setStatus(BillStatus.PAID);
            billRepo.save(bill);
            mailService.sendMail("doantuanbao2708@gmail.com", "Hệ thống điện", bill.getContract().getClient().getEmail(), "Thanh toán hóa đơn", "Hóa đơn" + bill.getCode() + " của bạn đã được thanh toán!");
        } catch (Exception e) {
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Thanh toán thất bại!");
        }
        return new ServerResponseDto(CommonConstant.SUCCESS, "Đã thanh toán thành công!");
    }

    @Override
    public ServerResponseDto getBillsCompany(String acronym, Date date) {
        Date endOfMonth = DateUtils.getEndOfMonth(date);
        List<BillDto> dtos = billRepo.findByCompanyAndStatusAndEndDate(acronym, null, endOfMonth);
        return new ServerResponseDto(CommonConstant.SUCCESS, dtos);
    }

    @Override
    public ServerResponseDto manualCreate(String contractName, String date) {
        try {
            Date dateTime = DateUtils.convertStringToDate(date, "yyyy-MM-dd");
            Date startOfMonth = DateUtils.getStartOfMonth(dateTime);
            Date endOfMonth = DateUtils.getEndOfMonth(dateTime);
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
            double consume = size == 0 ? 0.0 : calculateConsume(records, size);
            double normal = size == 0 ? 0.0 : calculateNormal(records, size);
            double low = size == 0 ? 0.0 : calculateLow(records, size);
            double high = size == 0 ? 0.0 : calculateHigh(records, size);
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
            mailService.sendMail("doantuanbao2708@gmail.com", "Hệ thống điện", bill.getContract().getClient().getEmail(), "Tạo hóa đơn", "Hóa đơn " + bill.getCode() + " của bạn đã được tạo!");
        } catch (Exception e) {
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Tạo hóa đơn thất bại!");
        }
        return new ServerResponseDto(CommonConstant.SUCCESS, "Hóa đơn được tạo thành công");
    }

    @Override
    public ServerResponseDto totalAnalyst(String acronym, String date) {
        Date dateTime = DateUtils.convertStringToDate(date, "yyyy-MM-dd");
        Date endOfMonth = DateUtils.getEndOfMonth(dateTime);
        Date endOf6Month = DateUtils.add(endOfMonth, Calendar.MONTH, -5);
        Date endOfNextMonth = DateUtils.add(endOfMonth, Calendar.MONTH, 1);
        List<TotalDto> values = new ArrayList<>();
        List<BillDto> dtos = billRepo.findByCompanyNotPaid(acronym, BillStatus.PAID, endOfMonth);
        while (endOf6Month.before(endOfNextMonth)) {
            double total = Optional.ofNullable(billRepo.countByCompanyAndStatusAndEndDate(acronym, null, endOf6Month)).orElse(0.0);
            values.add(new TotalDto(endOf6Month, total));
            endOf6Month = DateUtils.add(endOf6Month, Calendar.MONTH, 1);
        }
        Double total = values.get(values.size() - 1).getTotal();
        Double totalLast = values.get(values.size() - 2).getTotal();
        double paid = Optional.ofNullable(billRepo.countByCompanyAndStatusAndEndDate(acronym, BillStatus.PAID, endOfMonth)).orElse(0.0);
        double lastPaid = Optional.ofNullable(billRepo.countByCompanyAndStatusAndEndDate(acronym, BillStatus.PAID, DateUtils.add(endOfMonth, Calendar.MONTH, -1))).orElse(0.0);
        int requestDone = Optional.ofNullable(requestRepo.countRequestsDone(acronym, endOfMonth)).orElse(0);
        int requestLastDone = Optional.ofNullable(requestRepo.countRequestsDone(acronym, DateUtils.add(endOfMonth, Calendar.MONTH, -1))).orElse(0);
        AnalystData data = AnalystData.builder()
                .values(values)
                .bills(dtos)
                .total(total)
                .percentTotal((total - totalLast) / totalLast * 100)
                .paid(paid)
                .percentPaid((paid - lastPaid) / lastPaid * 100)
                .requests(requestRepo.countRequestsDone(acronym, endOfMonth))
                .percentRequests((double) (requestDone - requestLastDone) / requestLastDone * 100)
                .contracts(contractRepo.countContractsActive(acronym))
                .build();
        return new ServerResponseDto(CommonConstant.SUCCESS, data);
    }
}
