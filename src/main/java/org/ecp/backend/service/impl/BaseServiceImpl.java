package org.ecp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.constant.CommonConstant;
import org.ecp.backend.dto.BaseDto;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.entity.Base;
import org.ecp.backend.exception.ApplicationRuntimeException;
import org.ecp.backend.repository.BaseRepository;
import org.ecp.backend.service.BaseService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BaseServiceImpl implements BaseService {
    private final BaseRepository baseRepos;

    @Override
    public ServerResponseDto getAll() {
        return new ServerResponseDto(CommonConstant.SUCCESS, baseRepos.findAll());
    }
    @Override
    public ServerResponseDto create(BaseDto dto) {
        if (baseRepos.existsByObject(dto.getObject()))
            return new ServerResponseDto(CommonConstant.BAD_REQUEST, "Cấu hình đã tồn tại!");
        Base base = Base.builder()
                .object(dto.getObject())
                .value(dto.getValue())
                .build();
        baseRepos.save(base);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Tạo cấu hình thành công!");
    }

    @Override
    public ServerResponseDto update(BaseDto dto) {
        Base base = baseRepos.findByObject(dto.getObject()).orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Cấu hình không tồn tại!"));
        base.setValue(dto.getValue());
        baseRepos.save(base);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Cập nhật cấu hình thành công!");
    }

    @Override
    public ServerResponseDto delete(String object) {
        Base base = baseRepos.findByObject(object).orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Cấu hình không tồn tại!"));
        baseRepos.delete(base);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Xóa cấu hình thành công!");
    }
    @Override
    public ServerResponseDto getMessage() {
        Base base = baseRepos.findByObject(CommonConstant.BANK_ACCOUNT).orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Cấu hình không tồn tại!"));
        Base base1 = baseRepos.findByObject(CommonConstant.OWNER).orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Cấu hình không tồn tại!"));
        String message = "Thanh toán hóa đơn chuyển khoản đến tài khoản: " + base.getValue() + ". Chủ tài khoản: " + base1.getValue() + ".\nKhách hàng vui lòng chụp lại biên lai chuyển khoản và gửi cho chúng tôi để xác nhận thanh toán!";
        return new ServerResponseDto(CommonConstant.SUCCESS, message);
    }
}
