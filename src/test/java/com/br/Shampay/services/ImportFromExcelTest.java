package com.br.Shampay.services;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;
import static org.assertj.core.api.AssertionsForClassTypes.*;
@SpringBootTest
public class ImportFromExcelTest {
    @Autowired
    ImportFromExcel importFromExcel;
    @Test
    public void givenItauExtracCreateItauPayments() {
        try {
            try {
                importFromExcel.convertExcelFile();
            } catch (InvalidFormatException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertThat(true).isTrue();
    }
}
