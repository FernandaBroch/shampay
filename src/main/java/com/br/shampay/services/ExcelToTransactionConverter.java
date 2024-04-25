package com.br.shampay.services;

import com.br.shampay.entities.PaymentMethod;
import com.br.shampay.entities.TransactionLine;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
@Service
public class ExcelToTransactionConverter {

    @Autowired
    TransactionLineFactory transactionLineFactory;

    public Workbook importFiletoBuffer(String fileName) throws IOException {
        File baseDirectory = new File("src/main/resources/importFiles/");
        File fileInDirectory = new File(baseDirectory, fileName);
        FileInputStream fileInputStream = new FileInputStream(fileInDirectory);
        //OPCPackage file = OPCPackage.open(new File(filePath));
        return new HSSFWorkbook(fileInputStream);
    }

    public List<TransactionLine> convertExcelFileToTransactionLineList(String fileName, PaymentMethod paymentMethod) throws IOException, InvalidFormatException {
        Sheet sheet = importFiletoBuffer(fileName).getSheetAt(0);
        List<TransactionLine> transactionLineList = new ArrayList<>();
        Boolean startTableValues = false;
        TransactionLineGenerator transactionLineGenerator = transactionLineFactory.create(paymentMethod);

        for (Row row : sheet) {
            if(startTableValues){
                transactionLineList.add(transactionLineGenerator.transactionLineGenerator(row));
            }
            if(!startTableValues){
                startTableValues = transactionLineGenerator.isStartOfExtractValues(row);
            }
        }
        return transactionLineList.stream().filter(Objects::nonNull).toList();
    }
}
