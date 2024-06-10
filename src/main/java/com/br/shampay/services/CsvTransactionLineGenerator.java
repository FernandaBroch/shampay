package com.br.shampay.services;

import com.br.shampay.entities.TransactionLine;

import java.util.Map;

public interface CsvTransactionLineGenerator {
    public static final String COMMA_DELIMITER = ",";
    public static final int LAST_COLUMN_DESCRIPTION_INDEX = 4;
    public TransactionLine transactionLineGenerator(String csvLine);

}
