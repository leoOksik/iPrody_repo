package com.iprody.paymentserviceapp.async;

public interface AsyncListener<T extends Message> {
    void onMessage(T message);
}
