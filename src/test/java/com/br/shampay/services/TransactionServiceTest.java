package com.br.shampay.services;

import com.br.shampay.dto.TransactionShared;
import com.br.shampay.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@SpringBootTest
@ActiveProfiles("test" )
class TransactionServiceTest {
    @Autowired
    TransactionService transactionService;
    @Autowired
    UserService userService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
    TransactionLine transactionLine1 = new TransactionLine(LocalDate.parse("01/01/2024", formatter), "Pagamento de fatura", null, new BigDecimal("-2.90"), null, BudgetType.REALIZED, PaymentMethod.NUBANK, 1L, "EXTRATO1");
    TransactionLine transactionLine2 = new TransactionLine(LocalDate.parse("12/01/2024", formatter), "Transferencia Recebida Fulano de tal 999.999.999-99 - NU PAGAMENTOS  Agencia: 9 Conta: 9999999-1", null, new BigDecimal("100.00"), null, BudgetType.REALIZED, PaymentMethod.NUBANK, 1L, "EXTRATO1");
    @BeforeEach
    public void setup(){
        User user = new User(1L, "fulane");
        User user2 = new User(2L, "beltrane");
        userService.save(user);
        userService.save(user2);
    }
    @Test
    void givenListOfTransactionWhenPaymentMethodIsNubankCalculateTotalBalance() {

        List<Transaction> transactionList = new ArrayList<>();

        transactionList.add(transactionLine1.toTransaction());
        transactionList.add(transactionLine2.toTransaction());

        BigDecimal actualTransactionListTotal = transactionService.calculateTotalBalance(transactionList);
        BigDecimal expectedTransactionListTotal = transactionLine1.getTotalAmount().add(transactionLine2.getTotalAmount());

        assertThat(expectedTransactionListTotal).isEqualTo(actualTransactionListTotal);
    }

    @Test
    void givenTransactionSharedDataWithDefaultSharedCriteriaBuildNewTransaction() {
        TransactionShared transactionShared = new TransactionShared();
        transactionShared.setOriginalTransactionId(1L);
        transactionShared.setSharedUserId(2L);

        Transaction expectedTransaction = getExpectedTransaction();
        expectedTransaction.setSharedPercentage(0.5);
        expectedTransaction.setDueAmount(new BigDecimal("50.00"));

        Transaction transaction1 = transactionLine1.toTransaction();
        transaction1.setTotalAmount(new BigDecimal("100.0"));
        transaction1.setId(1L);

        Transaction actualTransaction = transactionService.createTransactionShared(transaction1, transactionShared );
        actualTransaction.setId(3L);

        assertThat(actualTransaction).isEqualTo(expectedTransaction);

    }
    @Test
    void givenTransactionSharedDataWithSharedPercentCriteriaBuildNewTransaction() {
        TransactionShared transactionShared = new TransactionShared();
        transactionShared.setOriginalTransactionId(1L);
        transactionShared.setSharedUserId(2L);
        transactionShared.setDuePercentage(0.3);

        Transaction expectedTransaction = getExpectedTransaction();
        expectedTransaction.setSharedPercentage(0.3);
        expectedTransaction.setDueAmount(new BigDecimal("30.00"));

        Transaction transaction1 = transactionLine1.toTransaction();
        transaction1.setTotalAmount(new BigDecimal("100.0"));
        transaction1.setId(1L);

        Transaction actualTransaction = transactionService.createTransactionShared(transaction1, transactionShared );
        actualTransaction.setId(3L);

        assertThat(actualTransaction).isEqualTo(expectedTransaction);

    }
    @Test
    void givenTransactionSharedDataWithSharedAmountCriteriaBuildNewTransaction() {
        TransactionShared transactionShared = new TransactionShared();
        transactionShared.setOriginalTransactionId(1L);
        transactionShared.setSharedUserId(2L);
        transactionShared.setDueAmount(new BigDecimal("10.00"));

        Transaction expectedTransaction = getExpectedTransaction();
        expectedTransaction.setSharedPercentage(0.1);
        expectedTransaction.setDueAmount(new BigDecimal("10.00"));

        Transaction transaction1 = transactionLine1.toTransaction();
        transaction1.setTotalAmount(new BigDecimal("100.0"));
        transaction1.setId(1L);

        Transaction actualTransaction = transactionService.createTransactionShared(transaction1, transactionShared );
        actualTransaction.setId(3L);

        assertThat(actualTransaction).isEqualTo(expectedTransaction);

    }

    private Transaction getExpectedTransaction() {
        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setId(3L);
        expectedTransaction.setDate(LocalDate.parse("01/01/2024", formatter));
        expectedTransaction.setImportedDescription("Pagamento de fatura");
        expectedTransaction.setBudgetType(BudgetType.REALIZED);
        expectedTransaction.setPaymentMethod(PaymentMethod.NUBANK);
        expectedTransaction.setOriginalTransactionId(1L);
        expectedTransaction.setPayerUserId(2L);

        return expectedTransaction;
    }

}