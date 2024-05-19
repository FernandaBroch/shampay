package com.br.shampay.controller;

import com.br.shampay.entities.PaymentMethod;
import com.br.shampay.entities.Transaction;
import com.br.shampay.services.CsvToTransactionConverter;
import com.br.shampay.services.ExcelToTransactionConverter;
import com.br.shampay.services.TransactionService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("transaction")
public class TransactionController {
    private static final String PATH_NAME = "src/main/resources/importFiles/";
    @Autowired
    TransactionService transactionService;
    @Autowired
    CsvToTransactionConverter csvToTransactionConverter;
    @Autowired
    ExcelToTransactionConverter excelToTransactionConverter;

    @PostMapping("/import")
    @ApiResponse(responseCode = "201" )
    public ResponseEntity<Void> importExtract(@RequestBody String fileName, PaymentMethod paymentMethod) throws IOException, InvalidFormatException {
        if(paymentMethod == PaymentMethod.ITAU) {
            transactionService.saveTransactions(excelToTransactionConverter.convertExcelFileToTransactionLineList(PATH_NAME, fileName, paymentMethod));
        }
        if(paymentMethod == PaymentMethod.NUBANK){
            transactionService.saveTransactions(csvToTransactionConverter.convertCsvFileToTransactionLineList(PATH_NAME, fileName));
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping
    @ApiResponse(responseCode = "201" )
    public ResponseEntity<Void> create(@RequestBody Transaction transaction ) throws IOException, InvalidFormatException {
        transactionService.save(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping("/{id}" )
    @ApiResponse(responseCode = "200")
    public ResponseEntity<Long> updateTransaction(@PathVariable(name ="id") Long id, @RequestBody Transaction updatedTransaction ){
        Transaction existingTransaction = transactionService.findById(id);
        updateTransactionNonNullProperties(existingTransaction, updatedTransaction);
        Transaction transactionUpdated = transactionService.save(existingTransaction);
        return ResponseEntity.status(HttpStatus.OK).body(transactionUpdated.getId());
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

    private void updateTransactionNonNullProperties(Transaction existingTransaction, Transaction updatedTransaction) {

        for (java.lang.reflect.Field field : Transaction.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object updatedValue = field.get(updatedTransaction);
                if (updatedValue != null) {
                    field.set(existingTransaction, updatedValue);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
