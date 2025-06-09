package com.menumitra.utilityclass;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;




public class DataDriven 
{
    public static Workbook workbook;
    public static Sheet sheet;
    public static Row row;
    public static Cell cell;
    public static FileInputStream fis;
    public static FileOutputStream fos;

    /**
     * Reads the Excel file and returns the specified sheet.
     * 
     * @param excelPath Path to the Excel file.
     * @param sheetName Name of the sheet to read.
     * @return Sheet object containing the data.
     * @throws CustomExceptions If there is an error while reading the file.
     */
    private static Sheet readExcelSheet(String excelPath, String sheetName) throws customException {
        try {
            fis = new FileInputStream(excelPath); // Open the file
            workbook = new XSSFWorkbook(fis); // Load the workbook
            sheet = workbook.getSheet(sheetName); // Get the specified sheet
            LogUtils.info("Excel sheet read successfully.");
            return sheet;
        } catch (IOException e) {
            LogUtils.error("Error reading the Excel file: " + e.getMessage());
            throw new customException("Error reading the Excel file: " + e.getMessage());
        } catch (Exception e) {
            LogUtils.error("Unexpected error occured while reading excel sheet " + e.getMessage());
            throw new customException("Unexpected error occured while reading excel sheet " + e.getMessage());
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                    LogUtils.info("Excel file closed successfully.");
                }
            } catch (IOException e) {
                LogUtils.error("Error closing excel file. ");
                throw new customException("Error closing excel file: " + e.getMessage());
            }
        }

    }

    /**
     * Reads data from an Excel sheet and returns it as a 2D array.
     * 
     * @param excelPath Path to the Excel file.
     * @param sheetName Name of the sheet to read.
     * @return 2D array containing the sheet data.
     * @throws CustomExceptions If there is an error while reading the data.
     */
    public static Object[][] readExcelData(String excelPath, String sheetName) throws customException {
        sheet = readExcelSheet(excelPath, sheetName);
        if (sheet == null) {
            throw new customException("Sheet '" + sheetName + "' not found in Excel file: " + excelPath);
        }

        // Get the last row with data
        int lastRow = sheet.getLastRowNum();
        if (lastRow < 0) {
            throw new customException("Excel sheet is empty: " + sheetName);
        }

        // Get the first row to determine column count
        Row firstRow = sheet.getRow(0);
        if (firstRow == null) {
            throw new customException("First row is empty in sheet: " + sheetName);
        }

        int lastCell = firstRow.getLastCellNum();
        if (lastCell < 0) {
            throw new customException("No columns found in first row of sheet: " + sheetName);
        }

        Object[][] data = new Object[lastRow + 1][lastCell];
        LogUtils.info("Processing Excel data: " + (lastRow + 1) + " rows, " + lastCell + " columns");

        try {
            // Iterate over each row
            for (int i = 0; i <= lastRow; i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    // Skip null rows but log them
                    LogUtils.warn("Null row found at index: " + i);
                    continue;
                }

                // Iterate over each cell in the row
                for (int j = 0; j < lastCell; j++) {
                    cell = row.getCell(j);
                    if (cell == null) {
                        // Handle null cells by setting empty string
                        data[i][j] = "";
                        continue;
                    }

                    // Handle cell data based on its type
                    try {
                        switch (cell.getCellTypeEnum()) {
                            case STRING:
                                data[i][j] = cell.getStringCellValue();
                                break;
                            case NUMERIC:
                                data[i][j] = String.valueOf((long) cell.getNumericCellValue());
                                break;
                            case BOOLEAN:
                                data[i][j] = String.valueOf(cell.getBooleanCellValue());
                                break;
                            case BLANK:
                                data[i][j] = "";
                                break;
                            default:
                                data[i][j] = ""; // Handle other types as empty string
                                break;
                        }
                    } catch (Exception e) {
                        LogUtils.warn("Error reading cell at row " + i + ", column " + j + ": " + e.getMessage());
                        data[i][j] = ""; // Set empty string on error
                    }
                }
            }
            LogUtils.info("Excel data read successfully.");
        } catch (Exception e) {
            LogUtils.error("Unexpected error occurred while reading excel sheet: " + e.getMessage());
            throw new customException("Unexpected error occurred while reading excel sheet: " + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                    LogUtils.info("Excel workbook closed successfully.");
                }
            } catch (IOException e) {
                LogUtils.error("Error closing excel workbook: " + e.getMessage());
                throw new customException("Error closing excel workbook: " + e.getMessage());
            }
        }
        return data;
    }

    /**
     * Reads Excel data and returns it as a list of maps where each map represents a row.
     * The map uses column index as key and cell value as value.
     * 
     * @param excelPath Path to the Excel file
     * @param sheetName Name of the sheet to read
     * @return List of maps containing row data
     * @throws customException If there is an error reading the Excel file
     */
    public static List<Map<Integer, String>> readExcelDataAsMap(String excelPath, String sheetName) throws customException {
        // Get the Excel sheet
        Sheet sheet = readExcelSheet(excelPath, sheetName);
        DataDriven obj = new DataDriven();
        
        // Get sheet dimensions
        int startRow = obj.getstartRowdata(sheet);
        int lastRow = sheet.getLastRowNum();
        int lastCell = sheet.getRow(startRow).getLastCellNum();
        
        List<Map<Integer, String>> excelData = new ArrayList<>();
        
        // Iterate through rows
        for (int rowIndex = startRow; rowIndex <= lastRow; rowIndex++) {
            Row currentRow = sheet.getRow(rowIndex);
            if (currentRow == null) continue;
            
            Map<Integer, String> rowData = new HashMap<>();
            
            // Iterate through cells in the row
            for (int columnIndex = 0; columnIndex < lastCell; columnIndex++) {
                Cell cell = currentRow.getCell(columnIndex);
                if (cell != null) {
                    try {
                        rowData.put(columnIndex, cell.getStringCellValue());
                    } catch (IllegalStateException e) {
                        // Handle non-string cell values
                        LogUtils.warn("Non-string value found at row " + rowIndex + ", column " + columnIndex);
                        rowData.put(columnIndex, String.valueOf(cell.getNumericCellValue()));
                    }
                }
            }
            excelData.add(rowData);
        }
        return excelData;
    }

    public  int getstartRowdata(Sheet sheet)
    {
        int startRow=0;
        int lastRow=sheet.getRow(0).getLastCellNum();
       
        for(int i=0;i<lastRow;i++)
        {
           if(sheet.getRow(i).getCell(i)!=null)
           {
        	   startRow=i;
        	   break;
           }
        }
        return startRow;
    }
}
