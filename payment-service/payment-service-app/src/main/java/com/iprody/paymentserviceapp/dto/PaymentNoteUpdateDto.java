package com.iprody.paymentserviceapp.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class PaymentNoteUpdateDto {
    @NotNull
    private String note;
}
