package com.example.carinspection.data.excel

import com.example.carinspection.data.model.DailyInspection
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ExcelDailyInspectionWriter {
    fun appendInspection(file: File, inspection: DailyInspection): File {
        val workbook = if (file.exists()) {
            FileInputStream(file).use { input -> XSSFWorkbook(input) }
        } else {
            XSSFWorkbook().apply {
                createSheet(SHEET_NAME).apply {
                    createRow(0).apply {
                        createCell(0).setCellValue("Date")
                        createCell(1).setCellValue("Driver Name")
                        createCell(2).setCellValue("Start Mileage")
                        createCell(3).setCellValue("Fuel Level %")
                        createCell(4).setCellValue("Oil Level OK")
                    }
                }
            }
        }

        val sheet = workbook.getSheet(SHEET_NAME)
        val nextRow = sheet.lastRowNum + 1
        sheet.createRow(nextRow).apply {
            createCell(0).setCellValue(inspection.date.toString())
            createCell(1).setCellValue(inspection.driverName)
            createCell(2).setCellValue(inspection.startMileage.toDouble())
            createCell(3).setCellValue(inspection.fuelLevelPercent.toDouble())
            createCell(4).setCellValue(inspection.oilLevelOk)
        }

        FileOutputStream(file).use { output ->
            workbook.write(output)
        }
        workbook.close()
        return file
    }

    companion object {
        private const val SHEET_NAME = "Daily Inspections"
    }
}
