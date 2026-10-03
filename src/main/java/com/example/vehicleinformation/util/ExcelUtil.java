package com.example.vehicleinformation.util;

import com.example.vehicleinformation.exception.ExcelFileException;
import com.example.vehicleinformation.model.Vehicle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Excel helpers for admin import/export. Primary storage is the database.
 */
public final class ExcelUtil {

    public static final String[] IMPORT_HEADERS = {
            "Vehicle Type", "Vehicle Number", "Block No", "House No", "Name", "Mobile Number"
    };

    private static final String[][] DEMO_ROWS = {
            {"Car", "GJ01AB1234", "F", "101", "Sample User", "9876543210"},
            {"Bike", "GJ01CD5678", "A", "202", "Demo Rider", "9123456780"}
    };

    private ExcelUtil() {
    }

    public static Workbook openWorkbook(InputStream in) {
        try {
            return new XSSFWorkbook(in);
        } catch (Exception e) {
            throw new ExcelFileException("Invalid or corrupted Excel file. Only .xlsx files are supported.", e);
        }
    }

    public static byte[] createDemoWorkbookBytes() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Vehicles");
            writeHeaderRow(sheet, workbook);
            for (int i = 0; i < DEMO_ROWS.length; i++) {
                Row row = sheet.createRow(i + 1);
                for (int c = 0; c < DEMO_ROWS[i].length; c++) {
                    row.createCell(c).setCellValue(DEMO_ROWS[i][c]);
                }
            }
            autosizeColumns(sheet, IMPORT_HEADERS.length);
            return toBytes(workbook);
        } catch (IOException e) {
            throw new ExcelFileException("Failed to create demo Excel file", e);
        }
    }

    public static byte[] createExportWorkbookBytes(List<Vehicle> vehicles) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Vehicles");
            writeHeaderRow(sheet, workbook);
            int rowIndex = 1;
            for (Vehicle vehicle : vehicles) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(nullToEmpty(vehicle.getVehicleType()));
                row.createCell(1).setCellValue(nullToEmpty(vehicle.getVehicleNumber()));
                row.createCell(2).setCellValue(nullToEmpty(vehicle.getBlockNo()));
                row.createCell(3).setCellValue(nullToEmpty(vehicle.getHouseNo()));
                row.createCell(4).setCellValue(nullToEmpty(vehicle.getName()));
                row.createCell(5).setCellValue(nullToEmpty(vehicle.getMobileNumber()));
            }
            autosizeColumns(sheet, IMPORT_HEADERS.length);
            return toBytes(workbook);
        } catch (IOException e) {
            throw new ExcelFileException("Failed to export Excel file", e);
        }
    }

    private static void writeHeaderRow(Sheet sheet, Workbook workbook) {
        Row header = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        for (int i = 0; i < IMPORT_HEADERS.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(IMPORT_HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private static void autosizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static byte[] toBytes(Workbook workbook) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return out.toByteArray();
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    public static List<Vehicle> readImportVehicles(Sheet sheet) {
        int[] columns = resolveImportColumnIndexes(sheet);
        List<Vehicle> vehicles = new ArrayList<>();
        int lastRow = sheet.getLastRowNum();
        for (int i = 1; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null || isImportRowEmpty(row, columns)) {
                continue;
            }
            vehicles.add(Vehicle.builder()
                    .vehicleType(getCellString(row, columns[0]))
                    .vehicleNumber(normalizeVehicleNumber(getCellString(row, columns[1])))
                    .blockNo(getCellString(row, columns[2]))
                    .houseNo(getCellString(row, columns[3]))
                    .name(getCellString(row, columns[4]))
                    .mobileNumber(getCellString(row, columns[5]))
                    .build());
        }
        return vehicles;
    }

    static int[] resolveImportColumnIndexes(Sheet sheet) {
        Row header = sheet.getRow(0);
        if (header == null) {
            throw new ExcelFileException("Import file is missing header row");
        }
        int[] indexes = new int[IMPORT_HEADERS.length];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = -1;
        }
        List<String> found = new ArrayList<>();
        int lastCell = Math.max(header.getLastCellNum(), 0);
        for (int i = 0; i < lastCell; i++) {
            String actual = normalizeHeader(getCellString(header, i));
            if (actual.isEmpty()) {
                continue;
            }
            found.add(getCellString(header, i).trim());
            for (int h = 0; h < IMPORT_HEADERS.length; h++) {
                if (normalizeHeader(IMPORT_HEADERS[h]).equals(actual)) {
                    indexes[h] = i;
                }
            }
        }
        List<String> missing = new ArrayList<>();
        for (int h = 0; h < IMPORT_HEADERS.length; h++) {
            if (indexes[h] < 0) {
                missing.add(IMPORT_HEADERS[h]);
            }
        }
        if (!missing.isEmpty()) {
            throw new ExcelFileException("Import file is missing required column(s): "
                    + String.join(", ", missing)
                    + ". Required columns are: " + String.join(", ", IMPORT_HEADERS)
                    + ". ID is optional and ignored."
                    + (found.isEmpty() ? "" : " Found: " + String.join(", ", found) + "."));
        }
        return indexes;
    }

    private static String normalizeHeader(String header) {
        if (header == null) {
            return "";
        }
        return header.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    public static String normalizeVehicleNumber(String vehicleNumber) {
        if (vehicleNumber == null) {
            return "";
        }
        return vehicleNumber.trim().replaceAll("\\s+", "").toUpperCase();
    }

    public static String normalizeMobile(String mobileNumber) {
        if (mobileNumber == null) {
            return "";
        }
        return mobileNumber.trim().replaceAll("[^0-9]", "");
    }

    private static boolean isImportRowEmpty(Row row, int[] columns) {
        for (int index : columns) {
            if (!getCellString(row, index).isBlank()) {
                return false;
            }
        }
        return true;
    }

    static String getCellString(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                double numeric = cell.getNumericCellValue();
                if (numeric == Math.floor(numeric)) {
                    yield String.valueOf((long) numeric);
                }
                yield String.valueOf(numeric);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue().trim();
                } catch (IllegalStateException ex) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> "";
        };
    }
}
