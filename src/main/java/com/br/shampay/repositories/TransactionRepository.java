package com.br.shampay.repositories;

import com.br.shampay.entities.BudgetType;
import com.br.shampay.entities.PaymentMethod;
import com.br.shampay.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByOriginalTransactionIdIsNotNull();
    List<Transaction> findByBudgetTypeAndPaymentMethod(BudgetType budgetType, PaymentMethod paymentMethod);
}
