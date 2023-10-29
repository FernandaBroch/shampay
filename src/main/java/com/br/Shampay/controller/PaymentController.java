package com.br.Shampay.controller;
import com.br.Shampay.entities.Payment;
import com.br.Shampay.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("payments")
public class PaymentController {
    @Autowired
    PaymentService paymentService;

    @PostMapping
    @ApiResponse(responseCode = "201" )
    public ResponseEntity<Long> create(@RequestBody Payment payment){
        Payment paymentResponse = paymentService.create(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponse.getId());
    }

    @GetMapping
    @ApiResponse(responseCode = "200")
    public ResponseEntity<List<Payment>> getTestimonies() {
        List<Payment> paymentListList = paymentService.findAll();
        if(paymentListList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(paymentListList);
    }

}
