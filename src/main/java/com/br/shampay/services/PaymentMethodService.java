package com.br.shampay.services;

import com.br.shampay.entities.PaymentMethod;
import com.br.shampay.entities.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentMethodService {
    public BigDecimal calculateTotalBalanceByPaymentMethod(List<Transaction> transactions, PaymentMethod paymentMethod){
        return transactions.stream()
                .filter(t -> t.getPaymentMethod().equals(paymentMethod))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
