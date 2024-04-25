package com.br.shampay.controller;

import com.br.shampay.entities.PaymentMethod;
import com.br.shampay.entities.Transaction;
import com.br.shampay.services.ExcelToTransactionConverter;
import com.br.shampay.services.TransactionService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("payments")
public class TransactionController {
    @Autowired
    TransactionService transactionService;
    @Autowired
    ExcelToTransactionConverter excelToTransactionConverter;

    @PostMapping
    @ApiResponse(responseCode = "201" )
    public ResponseEntity<Void> create(@RequestBody String fileName, PaymentMethod paymentMethod) throws IOException, InvalidFormatException {
        transactionService.saveTransactions(excelToTransactionConverter.convertExcelFileToTransactionLineList(fileName, paymentMethod));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @ApiResponse(responseCode = "200")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactionListList = transactionService.findAll();
        if(transactionListList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(transactionListList);
    }

}
