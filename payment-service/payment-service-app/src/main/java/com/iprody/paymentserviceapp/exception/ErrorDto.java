package com.iprody.paymentserviceapp.exception;

import java.time.Instant;

public record ErrorDto(int errorCode, String errorMessage, Instant timestamp) {

    public ErrorDto(int errorCode, String errorMessage) {
        this(errorCode, errorMessage, Instant.now());
    }
}
