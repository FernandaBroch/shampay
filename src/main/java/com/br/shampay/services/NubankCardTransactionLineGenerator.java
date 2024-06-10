package com.br.shampay.services;

import com.br.shampay.entities.BudgetType;
import com.br.shampay.entities.PaymentMethod;
import com.br.shampay.entities.TransactionLine;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@Service
public class NubankCardTransactionLineGenerator implements CsvTransactionLineGenerator {
    Map<String,Integer> columnNameOfIndex = Map.of("Date",0, "Category", 1, "Title", 2, "Amount", 3);

    public TransactionLine transactionLineGenerator(String csvLine) {
        TransactionLine transactionLine = new TransactionLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
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
            transactionLine.setDate(LocalDate.parse(validValues[columnNameOfIndex.get("Date")], formatter));
            transactionLine.setTotalAmount(new BigDecimal(validValues[columnNameOfIndex.get("Amount")]).multiply(BigDecimal.valueOf(-1)));
            transactionLine.setImportedDescription(validValues[columnNameOfIndex.get("Title")]);
        }
        else {
            transactionLine.setDate(LocalDate.parse(values[columnNameOfIndex.get("Date")], formatter));
            transactionLine.setTotalAmount(new BigDecimal(values[columnNameOfIndex.get("Amount")]).multiply(BigDecimal.valueOf(-1)));
            transactionLine.setImportedDescription(values[columnNameOfIndex.get("Title")]);
        }
        transactionLine.setPaymentMethod(PaymentMethod.CARD_NUBANK_MASTERCARD);
        transactionLine.setBudgetType(BudgetType.REALIZED);
        return transactionLine;
    }
}
