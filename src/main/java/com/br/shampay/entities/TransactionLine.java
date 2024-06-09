package com.br.shampay.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionLine {
    private LocalDate date;
    private String importedDescription;
    private String manualDescription;
    private BigDecimal totalAmount;
    private Category category;
    private BudgetType budgetType;
    private PaymentMethod paymentMethod;
    private Long payerUserId;
    private String originalFileName;

    public Transaction toTransaction(){
        return Transaction.builder()
                .date(date)
                .importedDescription(importedDescription)
                .manualDescription(manualDescription)
                .totalAmount(totalAmount)
                .category(category)
                .budgetType(budgetType)
                .paymentMethod(paymentMethod)
                .payerUserId(payerUserId)
                .originalFileName(originalFileName)
                .build();
    }
}


