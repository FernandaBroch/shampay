package com.br.shampay.services;

import com.br.shampay.entities.BudgetType;
import com.br.shampay.entities.PaymentMethod;
import com.br.shampay.entities.TransactionLine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@SpringBootTest
class CsvToTransactionConverterTest {

    @Autowired
    CsvToTransactionConverter csvToTransactionConverter;

    @Test
    public void givenNubankExtracCreateNubankTransactions() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
        List<TransactionLine> actualTransactionLineList;
        List<TransactionLine> expectedTransactionLineList = new ArrayList<>();

        TransactionLine transactionLine1 = new TransactionLine(LocalDate.parse("01/01/2024", formatter), "Pagamento de fatura", null, new BigDecimal("-2.90"), null, null, BudgetType.REALIZED, PaymentMethod.NUBANK);
        TransactionLine transactionLine2 = new TransactionLine(LocalDate.parse("12/01/2024", formatter), "Transferencia Recebida Fulano de tal 999.999.999-99 - NU PAGAMENTOS  Agencia: 9 Conta: 9999999-1", null, new BigDecimal("100.00"), null, null, BudgetType.REALIZED, PaymentMethod.NUBANK);

        expectedTransactionLineList.add(transactionLine1);
        expectedTransactionLineList.add(transactionLine2);

        actualTransactionLineList = csvToTransactionConverter.convertCsvFileToTransactionLineList("src/test/resources/", "TestNubankExtrato.csv");

        assertThat(expectedTransactionLineList).isEqualTo(actualTransactionLineList);


    }

}