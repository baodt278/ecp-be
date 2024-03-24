package org.ecp.backend.exception;

import org.ecp.backend.dto.response.ServerResponseDto;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HandlerException {
    @ExceptionHandler(ApplicationRuntimeException.class)
    public ServerResponseDto handleCustomException(ApplicationRuntimeException ex) {
        return new ServerResponseDto(ex.getCode(), ex.getMessage());
    }
}

