package org.ecp.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class ApplicationRuntimeException extends RuntimeException {
    private final int code;
    private final String message;
}
