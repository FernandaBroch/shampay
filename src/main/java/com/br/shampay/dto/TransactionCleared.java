package com.br.shampay.dto;

import com.br.shampay.entities.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionCleared {
    private LocalDate date;
    private String importedDescription;
    private BigDecimal totalAmount;
    private Long dueUserId;

}