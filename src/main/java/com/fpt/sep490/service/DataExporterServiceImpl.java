package com.fpt.sep490.service;

// Importing necessary libraries
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class DataExporterServiceImpl implements DataExporterService {

    // Method for exporting data to Excel
    private static final
    int FLUSH_THRESHOLD = 1000;
    public void exportToExcel(List<Map<String,Object>> dataList, OutputStream outputStream) throws IOException {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(FLUSH_THRESHOLD)) {
            SXSSFSheet sheet = workbook.createSheet("Data");
            Row headerRow = sheet.createRow(0);
            Set<String> headers = dataList.getFirst().keySet();
            int cellNum = 0;
            for (String header : headers) {
                headerRow.createCell(cellNum++).setCellValue(header);
            }

            int rowNum = 1;
            for (Map<String, Object> data : dataList) {
                Row row = sheet.createRow(rowNum++);
                cellNum = 0;
                for (Object value : data.values()) {
                    Cell cell = row.createCell(cellNum++);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }

                // Flush data in batches to save memory
                if (rowNum % FLUSH_THRESHOLD == 0) {
                    sheet.flushRows();
                }
            }
            workbook.write(outputStream);
        }
    }

    // Method for importing data from Excel
    public List<Map<String, Object>> importFromExcel(String filename) throws IOException {
        FileInputStream fis = new FileInputStream(new File(filename));
        Workbook workbook = WorkbookFactory.create(fis);

        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();

        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        int rowNum = 0;
        while(rows.hasNext()) {
            Row currentRow = rows.next();
            Map<String, Object> data = new LinkedHashMap<>();
            if(rowNum++ == 0) { // It's header row
                currentRow.forEach(cell -> headers.add(cell.getStringCellValue()));
            } else {
                int cellNum = 0;
                for(String header : headers) {
                    data.put(header, currentRow.getCell(cellNum++).getStringCellValue());
                }
                dataList.add(data);
            }
        }

        fis.close();
        workbook.close();
        return dataList;
    }
}
