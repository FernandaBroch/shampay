package com.br.shampay.services;

import com.br.shampay.entities.TransactionLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Service
public class CsvToTransactionConverter {

    @Autowired
    NubankTransactionLineGenerator nubankTransactionLineGenerator;

    public List<TransactionLine> convertCsvFileToTransactionLineList(String pathName, String fileName, Long payerUserId) {

        List<TransactionLine> transactionLineList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(pathName + fileName))) {
            String csvLine = br.readLine();
            while ((csvLine = br.readLine()) != null) {
                TransactionLine transactionLine = nubankTransactionLineGenerator.transactionLineGenerator(csvLine);
                transactionLine.setPayerUserId(payerUserId);
                transactionLineList.add(transactionLine);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return transactionLineList;
    }
}
