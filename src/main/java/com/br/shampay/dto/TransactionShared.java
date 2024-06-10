package com.br.shampay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionShared {
    private Long originalTransactionId;
    private Long sharedUserId;
    private Double duePercentage;
    private BigDecimal dueAmount;
}