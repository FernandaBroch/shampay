package com.br.shampay.services;

import com.br.shampay.entities.BudgetType;
import com.br.shampay.entities.PaymentMethod;
import com.br.shampay.entities.TransactionLine;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
@Service
public class NubankTransactionLineGenerator{

    public static final String COMMA_DELIMITER = ",";

    public static final int LAST_COLUMN_DESCRIPTION_INDEX = 4;

    public TransactionLine transactionLineGenerator(String csvLine) {
        TransactionLine transactionLine = new TransactionLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
        String[] values = csvLine.split(COMMA_DELIMITER);
        if(values.length > LAST_COLUMN_DESCRIPTION_INDEX) {
            String[] validValues = new String[LAST_COLUMN_DESCRIPTION_INDEX];
            int countValues = 0;
            for (String i : values){
                if(countValues >= LAST_COLUMN_DESCRIPTION_INDEX ){
                    values[LAST_COLUMN_DESCRIPTION_INDEX - 1] = values[LAST_COLUMN_DESCRIPTION_INDEX - 1] + values[countValues];
                }
                countValues++;
            }
            for(int i = 0; i < LAST_COLUMN_DESCRIPTION_INDEX; i++){
                validValues[i] = values[i];
            }
            transactionLine.setDate(LocalDate.parse(validValues[0], formatter));
            transactionLine.setTotalAmount(new BigDecimal(validValues[1]));
            transactionLine.setImportedDescription(validValues[3]);
        }
        else {
            transactionLine.setDate(LocalDate.parse(values[0], formatter));
            transactionLine.setTotalAmount(new BigDecimal(values[1]));
            transactionLine.setImportedDescription(values[3]);
        }
        transactionLine.setPaymentMethod(PaymentMethod.NUBANK);
        transactionLine.setBudgetType(BudgetType.REALIZED);
        return transactionLine;
    }
}
