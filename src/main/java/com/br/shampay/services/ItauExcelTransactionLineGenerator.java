package com.br.shampay.services;

import com.br.shampay.entities.BudgetType;
import com.br.shampay.entities.PaymentMethod;
import com.br.shampay.entities.TransactionLine;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;

public class ItauExcelTransactionLineGenerator implements ExcelTransactionLineGenerator {

    Map<String,Integer> columnNameOfIndex = Map.of("Date",0, "StatementDescription", 1, "Amount", 3);

    public Boolean isStartOfExtractValues(Row row){
        Cell cell = row.getCell(4);
        if(cell != null) {
            if (cell.getCellType() == CellType.STRING) {
                return cell.getRichStringCellValue().getString().equals("saldos (R$)");
            }
        }
        return false;
    }

    public TransactionLine transactionLineGenerator(Row row){
        TransactionLine transactionLine = new TransactionLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
        for (Cell cell : row) {
            switch (cell.getCellType()) {
                case STRING -> {
                    if (cell.getColumnIndex() == columnNameOfIndex.get("Date") & transactionLine.getDate() == null){
                        try{
                            transactionLine.setDate(LocalDate.parse(cell.getRichStringCellValue().getString(), formatter));
                        }catch (DateTimeParseException ex){
                            System.out.println("Nao achou data");
                        }
                    }
                    if (cell.getColumnIndex() == columnNameOfIndex.get("StatementDescription"))
                        transactionLine.setImportedDescription(cell.getRichStringCellValue().getString());
                }
                case NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        if (cell.getColumnIndex() == columnNameOfIndex.get("Date") & transactionLine.getDate() == null) {
                            transactionLine.setDate(cell.getDateCellValue().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate());
                        }
                    } else {
                        if (cell.getColumnIndex() == columnNameOfIndex.get("Amount"))
                            transactionLine.setTotalAmount(BigDecimal.valueOf(cell.getNumericCellValue()));
                    }
                }
            }
        }

        if (transactionLine.getDate() != null && transactionLine.getTotalAmount() != null) {
            transactionLine.setPaymentMethod(PaymentMethod.ITAU);
            transactionLine.setBudgetType(BudgetType.REALIZED);
            return transactionLine;
        }
        return null;
    }

}
