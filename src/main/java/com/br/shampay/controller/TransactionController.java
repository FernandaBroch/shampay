package com.br.shampay.controller;

import com.br.shampay.dto.TransactionShared;
import com.br.shampay.entities.*;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("transactions")
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
    public ResponseEntity<Void> importExtract(@RequestBody String fileName, PaymentMethod paymentMethod, Long payerUserId) throws IOException, InvalidFormatException {
        if(transactionService.findTransactionsByOriginalFileName(fileName).size() == 0) {
            if (paymentMethod == PaymentMethod.ITAU || paymentMethod == PaymentMethod.ITAU_CARD_LATAM || paymentMethod == PaymentMethod.ITAU_CARD_MASTERCARD || paymentMethod == PaymentMethod.ITAU_CARD_VISA) {
                transactionService.saveTransactions(excelToTransactionConverter.convertExcelFileToTransactionLineList(PATH_NAME, fileName, paymentMethod, payerUserId));
            }
            if (paymentMethod == PaymentMethod.NUBANK || paymentMethod == paymentMethod.NUBANK_CARD_MASTERCARD) {
                transactionService.saveTransactions(csvToTransactionConverter.convertCsvFileToTransactionLineList(PATH_NAME, fileName, paymentMethod, payerUserId));
            }
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
    @PutMapping("/shared/{id}" )
    @ApiResponse(responseCode = "200")
    public ResponseEntity<Long> createSharedTransaction(@RequestBody TransactionShared transactionSharedData){
        Transaction existingTransaction = transactionService.findById(transactionSharedData.getOriginalTransactionId());
        Transaction transactionShared = transactionService.createTransactionShared(existingTransaction, transactionSharedData);
        Transaction transactionSharedCreated = transactionService.findById(transactionShared.getId());
        transactionService.updateSharedFieldsOfOriginalTransaction(existingTransaction, transactionSharedCreated);
        return ResponseEntity.status(HttpStatus.OK).body(transactionSharedCreated.getId());
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
    @GetMapping("/balance")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<BigDecimal> getTransactionsBalanceByPaymentMethodAndBudgetType(PaymentMethod paymentMethod, BudgetType budgetType, Long userId) {
        List<Transaction> transactionListList = transactionService.findByBudgetTypeAndPaymentMethod(budgetType, paymentMethod, userId);
        BigDecimal transactionBalance = transactionService.calculateTotalBalance(transactionListList);
        return ResponseEntity.ok(transactionBalance);
    }
    @GetMapping("/balanceByStatementFile")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<BigDecimal> getTransactionsBalanceByPaymentMethodAndBudgetTypeAndStatementFile(PaymentMethod paymentMethod, Long userId, String statementFileName) {
        List<Transaction> transactionListList = transactionService.findByBudgetTypeAndPaymentMethod(BudgetType.REALIZED, paymentMethod, userId);
        BigDecimal transactionBalance = transactionService.calculateTotalBalance(transactionListList.stream().filter(transaction -> transaction.getOriginalFileName().equals(statementFileName)).toList());
        return ResponseEntity.ok(transactionBalance);
    }

    @GetMapping("/dueAmountByUser")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<Map<String, BigDecimal>> getDueAmountByUser() {
        return ResponseEntity.ok(transactionService.displayDueAmountByUser());
    }
    @GetMapping("/shared")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<List<Transaction>> getSharedTransactions() {
        List<Transaction> transactionListList = transactionService.findSharedTransaction();
        return ResponseEntity.ok(transactionListList);
    }
    @PostMapping("/clearing")
    @ApiResponse(responseCode = "201" )
    public ResponseEntity<Long> clearingDueAmount(@RequestBody TransactionLine transaction, Long dueUserId ) throws IOException, InvalidFormatException {
        transaction.setCategory(Category.TRANSFERENCE);
        transaction.setBudgetType(BudgetType.REALIZED);
        transaction.setOriginalFileName("MANUAL");
        Transaction createdTransaction = transactionService.save(transaction.toTransaction());

        TransactionShared transactionSharedData = new TransactionShared();
        transactionSharedData.setOriginalTransactionId(createdTransaction.getId());
        transactionSharedData.setDuePercentage(1.0);
        transactionSharedData.setSharedUserId(dueUserId);

        Transaction existingTransaction = transactionService.findById(transactionSharedData.getOriginalTransactionId());
        Transaction transactionShared = transactionService.createTransactionShared(existingTransaction, transactionSharedData);
        Transaction transactionSharedCreated = transactionService.findById(transactionShared.getId());
        transactionService.updateSharedFieldsOfOriginalTransaction(existingTransaction, transactionSharedCreated);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionSharedCreated.getId());
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
