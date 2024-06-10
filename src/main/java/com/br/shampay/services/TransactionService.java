package com.br.shampay.services;

import com.br.shampay.dto.TransactionShared;
import com.br.shampay.entities.BudgetType;
import com.br.shampay.entities.PaymentMethod;
import com.br.shampay.entities.Transaction;
import com.br.shampay.entities.TransactionLine;
import com.br.shampay.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {
    public static final Double DEFAULT_SHARED_PERCENTAGE = 0.5;
    @Autowired
    TransactionRepository transactionRepository;

    public Transaction save(Transaction transaction){
        transactionRepository.save(transaction);
        return transaction;
    }
    public List<Transaction> findAll(){
        return transactionRepository.findAll()
                .stream().toList();
    }
    public List<Transaction> findByBudgetTypeAndPaymentMethod(BudgetType budgetType, PaymentMethod paymentMethod){
        return transactionRepository.findByBudgetTypeAndPaymentMethod(budgetType, paymentMethod)
                .stream().toList();
    }
    public List<Transaction> findSharedTransaction(){
        return transactionRepository.findByOriginalTransactionIdIsNotNull()
                .stream().toList();
    }

    public Transaction findById(Long id){
        return transactionRepository.findById(id).get();

    }

    public void saveTransactions(List<TransactionLine> transactionLineList){
        for (TransactionLine transactionLine:transactionLineList ) {
            save(transactionLine.toTransaction());
        }
    }
    public BigDecimal calculateTotalBalance(List<Transaction> transactions){
        return transactions.stream()
                .map(Transaction::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public Transaction buildTransactionShared(Transaction transaction, TransactionShared transactionSharedData){
        Transaction transactionShared = new Transaction();
        if(transactionSharedData.getSharedUserId() != null){
            transactionShared.setDate(transaction.getDate());
            transactionShared.setImportedDescription(transaction.getImportedDescription());
            transactionShared.setManualDescription(transaction.getManualDescription());
            transactionShared.setCategory(transaction.getCategory());
            transactionShared.setBudgetType(transaction.getBudgetType());
            transactionShared.setPaymentMethod(transaction.getPaymentMethod());
            transactionShared.setOriginalTransactionId(transaction.getId());
            transactionShared.setPayerUserId(transactionSharedData.getSharedUserId());
            transactionShared.setSharedPercentage(calculateSharedPercentage(transaction, transactionSharedData));
            transactionShared.setDueAmount(calculateSharedAmount(transaction, transactionSharedData));
            this.save(transactionShared);
        }
        return transactionShared;
    }
    public List<Transaction> findTransactionsByOriginalFileName(String fileName){
        return transactionRepository.findByOriginalFileName(fileName);
    }
    public Transaction updateSharedFieldsOfOriginalTransaction(Transaction transaction, Transaction transactionShared){
        transaction.setPaidAmount(transaction.getTotalAmount().subtract(transactionShared.getDueAmount()));
        transaction.setSharedPercentage(1 - transactionShared.getSharedPercentage());
        transaction.setOriginalTransactionId(transaction.getId());
        transaction.setSharedTransactions(addTransactionSharedToTransactionSharedList(transaction, transactionShared.getId()));
        this.save(transaction);
        return transaction;
    }
    private BigDecimal calculateSharedAmount(Transaction transaction, TransactionShared transactionShared) {
        BigDecimal sharedAmount = null;
        switch (findTransactionSharedCriteria(transactionShared)){
            case "PERCENTAGE":
                sharedAmount = transaction.getTotalAmount().multiply(BigDecimal.valueOf(transactionShared.getSharedPercentage()));
                break;
            case "AMOUNT":
                sharedAmount = transactionShared.getDueAmount();
                break;
            default:
                sharedAmount = transaction.getTotalAmount().multiply(BigDecimal.valueOf(DEFAULT_SHARED_PERCENTAGE));;
        }
        return sharedAmount;
    }
    private Double calculateSharedPercentage(Transaction transaction, TransactionShared transactionShared) {
        Double sharedPercentage = null;
        switch (findTransactionSharedCriteria(transactionShared)){
            case "PERCENTAGE":
                sharedPercentage = transactionShared.getSharedPercentage();
                break;
            case "AMOUNT":
                sharedPercentage = transactionShared.getDueAmount().divide(transaction.getTotalAmount(), 4, RoundingMode.HALF_UP).doubleValue();
                break;
            default:
                sharedPercentage = DEFAULT_SHARED_PERCENTAGE;
        }
        return sharedPercentage;
    }
    private String findTransactionSharedCriteria(TransactionShared transaction){
        String transactionSharedCriteria = "DEFAULT";
        if (transaction.getSharedPercentage() != null && transaction.getSharedPercentage() > 0) {
            transactionSharedCriteria = "PERCENTAGE";
        }
        if(transaction.getDueAmount() != null && transaction.getDueAmount().compareTo(BigDecimal.ZERO) > 0){
            transactionSharedCriteria = "AMOUNT";
        }
        return transactionSharedCriteria;
    }
    private List<Long> addTransactionSharedToTransactionSharedList(Transaction transaction, Long transactionSharedId) {
        List<Long> transactionsShared = new ArrayList<>();
        if(transaction.getSharedTransactions() != null){
            transactionsShared.addAll(transaction.getSharedTransactions());
        }
        transactionsShared.add(transactionSharedId);
        return transactionsShared;
    }
}
