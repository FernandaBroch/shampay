package com.br.Shampay.services;

import com.br.Shampay.entities.BudgetType;
import com.br.Shampay.entities.Payment;
import com.br.Shampay.entities.PaymentMethod;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ImportFromExcel {
    public Workbook importFiletoBuffer() throws IOException {
        String filePath = "src/main/resources/importFiles/Extrato Conta Corrente-012023.xls";
        FileInputStream file = new FileInputStream(filePath);
        //OPCPackage file = OPCPackage.open(new File(filePath));
        return new HSSFWorkbook(file);
    }

    public List<Payment> convertExcelFile() throws IOException, InvalidFormatException {
        Sheet sheet = importFiletoBuffer().getSheetAt(0);
        List<Payment> paymentList = new ArrayList<>();
        Boolean startTableValues = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);

        int i = 0;
        for (Row row : sheet) {
            Payment payment = new Payment();
            for (Cell cell : row) {
                if (startTableValues) {
                    switch (cell.getCellType()) {
                        case STRING:
                            if (cell.getColumnIndex() == 0 & payment.getDate() == null)
                                payment.setDate(LocalDate.parse(cell.getRichStringCellValue().getString(), formatter));
                            if (cell.getColumnIndex() == 1)
                                payment.setImportedDescription(cell.getRichStringCellValue().getString());
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                if (cell.getColumnIndex() == 0 & payment.getDate() == null) {
                                    payment.setDate(cell.getDateCellValue().toInstant()
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate());
                                }
                            } else {
                                if (cell.getColumnIndex() == 3)
                                    payment.setAmount(BigDecimal.valueOf(cell.getNumericCellValue()));
                            }
                            break;
                    }
                }
                startTableValues = isStartOfExtractValues(cell, startTableValues);
            }
            i++;
            if (payment.getAmount() != null) {
                payment.setPaymentMethod(PaymentMethod.ITAU);
                payment.setBudgetType(BudgetType.REALIZED);
                paymentList.add(payment);
            }
        }
        System.out.println(paymentList);
        return paymentList;
    }
    public boolean isStartOfExtractValues(Cell cell, Boolean startTableValues){
        if (cell.getCellType() == CellType.STRING && !startTableValues) {
            if (cell.getRichStringCellValue().getString().equals("lançamentos"))
                return true;
        }
        return startTableValues;
    }
}
