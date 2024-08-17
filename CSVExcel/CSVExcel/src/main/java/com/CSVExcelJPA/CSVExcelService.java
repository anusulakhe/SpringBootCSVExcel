package com.CSVExcelJPA;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

@Service
public class CSVExcelService {

    public File exportToExcel(InputStream CSVPath) throws IOException {
        InputStreamReader reader = new InputStreamReader(CSVPath, StandardCharsets.UTF_8);
        CSVParser csvParser = null;
        Workbook workbook = null;
        File tempFile = File.createTempFile("data-", ".xlsx");

        try {
            csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
            Iterator<CSVRecord> csvRecordIterator = csvParser.iterator();

            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Sheet1");

            if (csvRecordIterator.hasNext()) {
                CSVRecord headerRecord = csvRecordIterator.next();
                Row headerRow = sheet.createRow(0);
                int headerIndex = 0;
                for (String header : headerRecord.toMap().keySet()) {
                    headerRow.createCell(headerIndex++).setCellValue(header);
                }
            }

            int rowIndex = 1;
            while (csvRecordIterator.hasNext()) {
                CSVRecord record = csvRecordIterator.next();
                Row row = sheet.createRow(rowIndex++);
                int cellIndex = 0;
                for (String value : record) {
                    row.createCell(cellIndex++).setCellValue(value);
                }
            }

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                workbook.write(out);
            }

            return tempFile;
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            if (csvParser != null) {
                csvParser.close();
            }
        }
    }
}
