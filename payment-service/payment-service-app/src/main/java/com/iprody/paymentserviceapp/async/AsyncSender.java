package com.iprody.paymentserviceapp.async;

public interface AsyncSender<T extends Message> {
    void send(T message);
}
