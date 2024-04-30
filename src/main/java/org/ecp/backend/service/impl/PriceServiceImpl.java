package org.ecp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.constant.CommonConstant;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.entity.Price;
import org.ecp.backend.repository.PriceRepository;
import org.ecp.backend.service.PriceService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {
    private final PriceRepository priceRepo;
    @Override
    public ServerResponseDto getAll() {
        return new ServerResponseDto(CommonConstant.SUCCESS, priceRepo.findAllPrices());
    }

    @Override
    public ServerResponseDto update(long id, double value) {
        Price price = priceRepo.findById(id).orElseGet(Price::new);
        price.setPrice(value);
        priceRepo.save(price);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Cập nhật giá thành công!");
    }

    @Override
    public ServerResponseDto delete(long id) {
        priceRepo.deleteById(id);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Xóa giá thành công!");
    }
}
