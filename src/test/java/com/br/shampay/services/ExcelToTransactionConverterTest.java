package com.br.shampay.services;

import com.br.shampay.entities.BudgetType;
import com.br.shampay.entities.PaymentMethod;
import com.br.shampay.entities.TransactionLine;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.*;
@SpringBootTest
public class ExcelToTransactionConverterTest {
    @Autowired
    ExcelToTransactionConverter excelToTransactionConverter;

    @Test
    public void givenItauExtracCreateItauTransactions() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
        List<TransactionLine> actualTransactionLineList;
        TransactionLine transactionLine1 = new TransactionLine(LocalDate.parse("11/12/2023", formatter), "PIX TRANSF ", null, new BigDecimal("100.0"), null, BudgetType.REALIZED, PaymentMethod.ITAU, 1L, "TestItauExtrato.xls");
        TransactionLine transactionLine2 = new TransactionLine(LocalDate.parse("18/12/2023", formatter), "REND PAGO APLIC AUT MAIS", null, new BigDecimal("0.71"), null, BudgetType.REALIZED, PaymentMethod.ITAU, 1L, "TestItauExtrato.xls");

        List<TransactionLine> expectedTransactionLineList = new ArrayList<>();
        expectedTransactionLineList.add(transactionLine1);
        expectedTransactionLineList.add(transactionLine2);
        try {
            try {
                actualTransactionLineList = excelToTransactionConverter.convertExcelFileToTransactionLineList( "src/test/resources/","TestItauExtrato.xls", PaymentMethod.ITAU, 1L);
            } catch (InvalidFormatException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertThat(expectedTransactionLineList).isEqualTo(actualTransactionLineList);
    }
}
