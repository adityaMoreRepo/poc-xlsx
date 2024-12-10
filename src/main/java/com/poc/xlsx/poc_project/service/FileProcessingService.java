package com.poc.xlsx.poc_project.service;

import com.poc.xlsx.poc_project.exception.MissingHeaderException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileProcessingService {
    public List<String> getSheetNames(Path filePath) throws IOException {
        List<String> sheetNames = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(Files.newInputStream(filePath))) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheetNames.add(workbook.getSheetName(i));
            }
        }
        return sheetNames;
    }

    public List<Map<String, String>> parseExcel(Path filePath) throws IOException {
        List<Map<String, String>> rows = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(Files.newInputStream(filePath))) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                Map<String, String> rowData = new HashMap<>();
                for (Cell cell : row) {
                    rowData.put(headerRow.getCell(cell.getColumnIndex()).getStringCellValue(),
                            cell.toString());
                }
                rows.add(rowData);
            }
        }
        return rows;
    }

    public List<Map<String, String>> parseExcelTable(Path filePath, String sheetName) throws IOException {
        //Creating local costants for row and column numbers
        int startRow = 9;
        int endRow = 15;
        int startCol = 1;
        int endCol = 19;
        List<Map<String, String>> tableData = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(Files.newInputStream(filePath))) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new SheetNotFoundException("Sheet with name " + sheetName + " was not found.");
            }
            // Read the header row
            Row headerRow = sheet.getRow(startRow);
            //for testing making endrow = lastrownum
//            endRow = sheet.getLastRowNum();
            if (headerRow == null) {
                throw new MissingHeaderException("Header row is missing or invalid.");
            }

            // Parse rows
            for (int i = startRow + 3; i <= endRow; i++) { // Starting after the header row
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, String> rowData = new HashMap<>();
                for (int j = startCol; j <= endCol; j++) {
                    Cell cell = row.getCell(j);
                    Cell headerCell = headerRow.getCell(j);

                    // Ensure headers and data cells exist
                    if (cell != null && headerCell != null) {
                        String header = getStringCellValue(headerCell).replace(" ", "");
                        String value = getStringCellValue(cell);
                        rowData.put(header, value);
                    }
                }
                tableData.add(rowData);
            }
        }
        return tableData;
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.format("%.0f", cell.getNumericCellValue()); // Avoid scientific notation
                }
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue(); // Evaluate as string
                } catch (IllegalStateException e) {
                    return String.valueOf(cell.getNumericCellValue()); // Fallback for numeric formulas
                }
            case BLANK:
                return "";
            default:
                return cell.toString();
        }
    }

}
