package com.br.shampay.services;

import com.br.shampay.entities.TransactionLine;
import org.apache.poi.ss.usermodel.Row;

public interface TransactionLineGenerator {

    Boolean isStartOfExtractValues(Row row);
    TransactionLine transactionLineGenerator(Row row);

}
