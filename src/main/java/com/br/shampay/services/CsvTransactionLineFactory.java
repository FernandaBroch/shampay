package com.br.shampay.services;

import com.br.shampay.entities.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class CsvTransactionLineFactory {
    public CsvTransactionLineGenerator create(PaymentMethod paymentMethod){
        if(paymentMethod.equals(PaymentMethod.NUBANK)){
            return new NubankTransactionLineGenerator();
        } else if (paymentMethod.equals(PaymentMethod.NUBANK_CARD_MASTERCARD)) {
            return new NubankCardTransactionLineGenerator();
        }
        return null;
    }

}
