package com.br.shampay.services;

import com.br.shampay.entities.BudgetType;
import com.br.shampay.entities.PaymentMethod;
import com.br.shampay.entities.TransactionLine;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;
@AllArgsConstructor
@NoArgsConstructor
public class ItauCardExcelTransactionLineGenerator implements ExcelTransactionLineGenerator {
    private PaymentMethod paymentMethod;

    Map<String,Integer> columnNameOfIndex = Map.of("Date",0, "StatementDescription", 1, "Amount", 3);
    TransactionLine transactionLineDolar = new TransactionLine();
    TransactionLine transactionLineIOF = new TransactionLine();

    public ItauCardExcelTransactionLineGenerator(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Boolean isStartOfExtractValues(Row row){
        Cell cell = row.getCell(3);
        if(cell != null) {
            if (cell.getCellType() == CellType.STRING) {
                return cell.getRichStringCellValue().getString().equals("valor");
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
                    if (cell.getColumnIndex() == columnNameOfIndex.get("Date") && transactionLine.getDate() == null) {
                        try {
                            transactionLine.setDate(LocalDate.parse(cell.getRichStringCellValue().getString(), formatter));
                        }catch (DateTimeParseException ex){
                            if(cell.getRichStringCellValue().getString() != null && cell.getRichStringCellValue().getString().equals("IOF - transação internacional"))
                                transactionLine.setImportedDescription(cell.getRichStringCellValue().getString());
                            System.out.println("Nao achou data");
                        }
                    }
                    if (cell.getColumnIndex() == columnNameOfIndex.get("StatementDescription") && transactionLine.getImportedDescription() == null)
                        transactionLine.setImportedDescription(cell.getRichStringCellValue().getString());
                }
                case NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        if (cell.getColumnIndex() == columnNameOfIndex.get("Date") && transactionLine.getDate() == null) {
                            transactionLine.setDate(cell.getDateCellValue()
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate());
                        }
                    } else {
                        if (cell.getColumnIndex() == columnNameOfIndex.get("Amount"))
                            transactionLine.setTotalAmount(BigDecimal.valueOf(cell.getNumericCellValue()).multiply(BigDecimal.valueOf(-1)));
                    }
                }
            }
        }
        if(transactionLineDolar.getDate() != null){
            transactionLine.setDate(transactionLineDolar.getDate());
            transactionLine.setBudgetType(transactionLineDolar.getBudgetType());
            transactionLine.setPaymentMethod(transactionLineDolar.getPaymentMethod());
            transactionLineDolar = new TransactionLine();
            return transactionLine;
        }else if ("IOF - transação internacional".equals(transactionLine.getImportedDescription())) {
            transactionLine.setDate(transactionLineIOF.getDate());
            transactionLine.setBudgetType(transactionLineIOF.getBudgetType());
            transactionLine.setPaymentMethod(transactionLineIOF.getPaymentMethod());
            transactionLineIOF = new TransactionLine();
            return transactionLine;
        } else if ("SALDO PARCIAL".equals(transactionLine.getImportedDescription()) ||
                   "SALDO FINAL".equals(transactionLine.getImportedDescription()) ||
                   "APL APLIC AUT MAIS".equals(transactionLine.getImportedDescription()) ||
                   "RES APLIC AUT MAIS".equals(transactionLine.getImportedDescription())) {
            return null;
        } else if (transactionLine.getDate() != null) {
            transactionLine.setPaymentMethod(this.paymentMethod);
            transactionLine.setBudgetType(BudgetType.REALIZED);
            if (transactionLine.getImportedDescription().equals("dólar de conversão")) {
                transactionLineDolar = transactionLine;
                transactionLineIOF = transactionLine;
                return null;
            }
            return transactionLine;
        }
        return null;
    }

}
