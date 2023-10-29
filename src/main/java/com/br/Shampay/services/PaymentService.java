package com.br.Shampay.services;

import com.br.Shampay.entities.Payment;
import com.br.Shampay.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;

    public Payment create(Payment payment){
        paymentRepository.save(payment);
        return payment;
    }
    public List<Payment> findAll(){
        return paymentRepository.findAll()
                .stream()
                .collect(Collectors.toList());
    }

}
