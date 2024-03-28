package org.ecp.backend.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class ApplicationRuntimeException extends RuntimeException {
    private final int code;
    private final String message;
}
