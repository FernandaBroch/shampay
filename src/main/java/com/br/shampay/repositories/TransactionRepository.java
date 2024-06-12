package com.br.shampay.repositories;

import com.br.shampay.entities.BudgetType;
import com.br.shampay.entities.PaymentMethod;
import com.br.shampay.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByOriginalTransactionIdIsNotNull();
    List<Transaction> findByBudgetTypeAndPaymentMethodAndPayerUserId(BudgetType budgetType, PaymentMethod paymentMethod, Long payerUserId);
    List<Transaction> findByOriginalFileName(String originalFileName);
}
