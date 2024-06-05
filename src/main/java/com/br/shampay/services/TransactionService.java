package com.br.shampay.services;

import com.br.shampay.entities.BudgetType;
import com.br.shampay.entities.PaymentMethod;
import com.br.shampay.entities.Transaction;
import com.br.shampay.entities.TransactionLine;
import com.br.shampay.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {
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
    public List<Transaction> findSharedTransaction(Boolean shared){
        return transactionRepository.findByShared(shared)
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
}
