package com.br.shampay.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDate date;
    private String importedDescription;
    private String manualDescription;
    private BigDecimal totalAmount;
    private Boolean shared;
    private Category category;
    private BudgetType budgetType;
    private PaymentMethod paymentMethod;
    private Long payerUserId;
    private String originalFileName;

    private Long originalTransactionId;
    private List<Long> sharedUsersId;
    private Double sharedPercentage;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;
}
