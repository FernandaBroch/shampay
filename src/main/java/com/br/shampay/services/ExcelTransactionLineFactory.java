package com.br.shampay.services;

import com.br.shampay.entities.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class ExcelTransactionLineFactory {
    public ExcelTransactionLineGenerator create(PaymentMethod paymentMethod){
        if(paymentMethod.equals(PaymentMethod.ITAU)){
            return new ItauExcelTransactionLineGenerator();
        } else if (paymentMethod.equals(PaymentMethod.ITAU_CARD_LATAM) ||
                paymentMethod.equals(PaymentMethod.ITAU_CARD_VISA) ||
                paymentMethod.equals(PaymentMethod.ITAU_CARD_MASTERCARD)){
            return new ItauCardExcelTransactionLineGenerator(paymentMethod);
        }
        return null;
    }

}
