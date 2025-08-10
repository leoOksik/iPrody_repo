package com.iprody.paymentserviceapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Class<?> entity, UUID entityId, OperationError operation) {
        super("Entity %s with id %s not found. Operation -> %s".formatted(entity.getSimpleName(), entityId, operation));
    }
}
