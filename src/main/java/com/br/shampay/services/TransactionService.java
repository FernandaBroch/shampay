package com.br.shampay.services;

import com.br.shampay.dto.TransactionShared;
import com.br.shampay.entities.*;
import com.br.shampay.repositories.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@AllArgsConstructor
public class TransactionService {
    public static final Double DEFAULT_SHARED_PERCENTAGE = 0.5;

    TransactionRepository transactionRepository;

    UserService userService;

    public Transaction save(Transaction transaction){
        transactionRepository.save(transaction);
        return transaction;
    }
    public List<Transaction> findAll(){
        return transactionRepository.findAll()
                .stream().toList();
    }
    public List<Transaction> findByBudgetTypeAndPaymentMethod(BudgetType budgetType, PaymentMethod paymentMethod, Long payerUserId){
        return transactionRepository.findByBudgetTypeAndPaymentMethodAndPayerUserId(budgetType, paymentMethod, payerUserId)
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
                .filter(Objects::nonNull)
                .reduce((prev, next) -> prev.add(next))
                .orElse(BigDecimal.ZERO);
    }
    public Transaction createTransactionShared(Transaction transaction, TransactionShared transactionSharedData){
        Transaction transactionShared = new Transaction();
        if(userService.findUserById(transactionSharedData.getSharedUserId()).isPresent()){
            transactionShared.setDate(transaction.getDate());
            transactionShared.setImportedDescription(transaction.getImportedDescription());
            transactionShared.setManualDescription(transaction.getManualDescription());
            transactionShared.setCategory(transaction.getCategory());
            transactionShared.setBudgetType(transaction.getBudgetType());
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
        if(transactionShared.getDueAmount() != null) {
            transaction.setPaidAmount(transaction.getTotalAmount().subtract(transactionShared.getDueAmount()));
            transaction.setSharedPercentage(1 - transactionShared.getSharedPercentage());
            transaction.setOriginalTransactionId(transaction.getId());
            transaction.setSharedTransactions(addTransactionSharedToTransactionSharedList(transaction, transactionShared.getId()));
            this.save(transaction);
        }
        return transaction;
    }
    public Map<String, BigDecimal> displayDueAmountByUser(){
        Map<String,BigDecimal> map = new HashMap<>();
        List<User> users = userService.findAll();
        for (User user:users) {
            map.put(user.getName(), calculateDueAmountByUser(user.getId()));
        }
        Optional<Map.Entry<String, BigDecimal>> biggestDebit = map.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());

        map.put("Pay to " + biggestDebit.get().getKey(), map.values()
                .stream()
                .reduce((prev, next) -> prev.subtract(next))
                .orElse(BigDecimal.ZERO));
        return map;
    }
    public BigDecimal calculateDueAmountByUser(Long userId){
        List<Transaction> transactions = findAll();
        return transactions.stream()
                .filter(transaction -> transaction.getPayerUserId().equals(userId))
                .filter(transaction -> transaction.getDueAmount() != null)
                .map(Transaction::getDueAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private BigDecimal calculateSharedAmount(Transaction transaction, TransactionShared transactionShared) {
        BigDecimal sharedAmount = null;
        switch (findTransactionSharedCriteria(transactionShared)){
            case "PERCENTAGE":
                sharedAmount = transaction.getTotalAmount().multiply(BigDecimal.valueOf(transactionShared.getDuePercentage()));
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
                sharedPercentage = transactionShared.getDuePercentage();
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
        if (transaction.getDuePercentage() != null && transaction.getDuePercentage() > 0) {
            transactionSharedCriteria = "PERCENTAGE";
        } else if(transaction.getDueAmount() != null && transaction.getDueAmount().compareTo(BigDecimal.ZERO) > 0){
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
