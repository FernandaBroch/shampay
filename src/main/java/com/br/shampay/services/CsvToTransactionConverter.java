package com.br.shampay.services;

import com.br.shampay.entities.PaymentMethod;
import com.br.shampay.entities.TransactionLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Service
public class CsvToTransactionConverter {

    @Autowired
    CsvTransactionLineFactory csvTransactionLineFactory;

    public List<TransactionLine> convertCsvFileToTransactionLineList(String pathName, String fileName, PaymentMethod paymentMethod, Long payerUserId) {

        List<TransactionLine> transactionLineList = new ArrayList<>();
        CsvTransactionLineGenerator csvTransactionLineGenerator = csvTransactionLineFactory.create(paymentMethod);

        try (BufferedReader br = new BufferedReader(new FileReader(pathName + fileName))) {
            String csvLine = br.readLine();
            while ((csvLine = br.readLine()) != null) {
                TransactionLine transactionLine = csvTransactionLineGenerator.transactionLineGenerator(csvLine);
                transactionLine.setPayerUserId(payerUserId);
                transactionLine.setOriginalFileName(fileName);
                transactionLineList.add(transactionLine);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return transactionLineList;
    }
}
