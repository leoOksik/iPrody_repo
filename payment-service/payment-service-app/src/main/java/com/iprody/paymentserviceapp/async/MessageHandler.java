package com.iprody.paymentserviceapp.async;

public interface MessageHandler<T extends Message> {
    void handle(T message);
}
