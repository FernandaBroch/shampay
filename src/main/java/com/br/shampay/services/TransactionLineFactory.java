package com.br.shampay.services;

import com.br.shampay.entities.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class TransactionLineFactory {
    public TransactionLineGenerator create(PaymentMethod paymentMethod){
        if(paymentMethod.equals(PaymentMethod.ITAU)){
            return new ItauTransactionLineGenerator();
        }
        return null;
    }

}
