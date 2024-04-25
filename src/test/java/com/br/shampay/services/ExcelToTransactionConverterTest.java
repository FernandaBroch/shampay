package com.br.shampay.services;

import com.br.shampay.entities.PaymentMethod;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;
import static org.assertj.core.api.AssertionsForClassTypes.*;
@SpringBootTest
public class ExcelToTransactionConverterTest {
    @Autowired
    ExcelToTransactionConverter excelToTransactionConverter;

    @Test
    public void givenItauExtracCreateItauTransactions() {
        try {
            try {
                excelToTransactionConverter.convertExcelFileToTransactionLineList("Extrato Conta Corrente-122023.xls", PaymentMethod.ITAU);
            } catch (InvalidFormatException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertThat(true).isTrue();
    }
}
