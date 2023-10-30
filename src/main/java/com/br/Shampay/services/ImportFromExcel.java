package com.br.Shampay.services;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportFromExcel {
    public Workbook importFiletoBuffer() throws IOException {
        String filePath = "src/main/resources/importFiles/Extrato Conta Corrente-012023.xls";
        FileInputStream file = new FileInputStream(filePath);
        //OPCPackage file = OPCPackage.open(new File(filePath));
        return new HSSFWorkbook(file);

    }

    public Map convertExcelFile() throws IOException, InvalidFormatException {
        Sheet sheet = importFiletoBuffer().getSheetAt(0);

        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;
        for (
                Row row : sheet) {
            data.put(i, new ArrayList<>());
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case STRING:
                        data.get(i).add(cell.getRichStringCellValue().getString());
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            data.get(i).add(cell.getDateCellValue() + "");
                        } else {
                            data.get(i).add(cell.getNumericCellValue() + "");
                        }
                        break;
                    case BOOLEAN: data.get(i).add(cell.getBooleanCellValue() + "");
                        break;
                    case FORMULA: data.get(i).add(cell.getCellFormula() + "");
                        break;
                    default:
                        data.get(i).add(" ");
                }
            }
            i++;
        }
        System.out.println(data);
        return data;
    }
}
