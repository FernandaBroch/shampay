package com.br.shampay.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "sharedTransaction")
public class SharedTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long transactionId;
    private Long userId;
    private Double sharedPercentage;
    private BigDecimal sharedAmount;
    private BigDecimal totalAmount;

}
