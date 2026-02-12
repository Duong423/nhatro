package com.example.nhatro.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.example.nhatro.dto.response.PaymentHistoryResponseDTO;
import com.example.nhatro.service.PaymentHistoryExcelService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentHistoryExcelServiceImpl implements PaymentHistoryExcelService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public ByteArrayOutputStream exportPaymentHistoryToExcel(List<PaymentHistoryResponseDTO> histories, int month, int year) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Lịch sử thanh toán");

            // ===== Tạo các styles =====
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            CellStyle totalLabelStyle = createTotalLabelStyle(workbook);
            CellStyle totalValueStyle = createTotalValueStyle(workbook);

            int rowIndex = 0;

            // ===== Row 0: Tiêu đề =====
            Row titleRow = sheet.createRow(rowIndex++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BÁO CÁO LỊCH SỬ THANH TOÁN - THÁNG " + month + "/" + year);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));

            // Row 1: Dòng trống
            rowIndex++;

            // ===== Row 2: Header =====
            Row headerRow = sheet.createRow(rowIndex++);
            String[] headers = {
                    "STT", "Mã phòng", "Tên người thuê", "SĐT người thuê",
                    "Tiền phòng", "Tiền điện", "Tiền nước", "Tiền dịch vụ",
                    "Tổng tiền", "Phương thức TT", "Mã giao dịch",
                    "Ngày thanh toán", "Hạn thanh toán", "Ghi chú"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ===== Rows: Dữ liệu =====
            BigDecimal totalRoomPrice = BigDecimal.ZERO;
            BigDecimal totalElectricity = BigDecimal.ZERO;
            BigDecimal totalWater = BigDecimal.ZERO;
            BigDecimal totalService = BigDecimal.ZERO;
            BigDecimal grandTotal = BigDecimal.ZERO;

            int stt = 1;
            for (PaymentHistoryResponseDTO history : histories) {
                Row dataRow = sheet.createRow(rowIndex++);
                int colIndex = 0;

                // STT
                createCell(dataRow, colIndex++, stt++, dataStyle);

                // Mã phòng
                createCell(dataRow, colIndex++, history.getRoomCode(), dataStyle);

                // Tên người thuê
                createCell(dataRow, colIndex++, history.getTenantName(), dataStyle);

                // SĐT người thuê
                createCell(dataRow, colIndex++, history.getTenantPhone(), dataStyle);

                // Tiền phòng
                BigDecimal roomPrice = history.getRoomPrice() != null ? history.getRoomPrice() : BigDecimal.ZERO;
                createCurrencyCell(dataRow, colIndex++, roomPrice, currencyStyle);
                totalRoomPrice = totalRoomPrice.add(roomPrice);

                // Tiền điện
                BigDecimal electricity = history.getElectricityCost() != null ? history.getElectricityCost() : BigDecimal.ZERO;
                createCurrencyCell(dataRow, colIndex++, electricity, currencyStyle);
                totalElectricity = totalElectricity.add(electricity);

                // Tiền nước
                BigDecimal water = history.getWaterCost() != null ? history.getWaterCost() : BigDecimal.ZERO;
                createCurrencyCell(dataRow, colIndex++, water, currencyStyle);
                totalWater = totalWater.add(water);

                // Tiền dịch vụ
                BigDecimal service = history.getServiceCost() != null ? history.getServiceCost() : BigDecimal.ZERO;
                createCurrencyCell(dataRow, colIndex++, service, currencyStyle);
                totalService = totalService.add(service);

                // Tổng tiền
                BigDecimal total = history.getTotalAmount() != null ? history.getTotalAmount() : BigDecimal.ZERO;
                createCurrencyCell(dataRow, colIndex++, total, currencyStyle);
                grandTotal = grandTotal.add(total);

                // Phương thức thanh toán
                createCell(dataRow, colIndex++, history.getPaymentMethod(), dataStyle);

                // Mã giao dịch
                createCell(dataRow, colIndex++, history.getTransactionCode(), dataStyle);

                // Ngày thanh toán
                String paymentDate = history.getPaymentDate() != null
                        ? history.getPaymentDate().format(DATE_FORMATTER)
                        : "";
                createCell(dataRow, colIndex++, paymentDate, dataStyle);

                // Hạn thanh toán
                String dueDate = history.getDueDate() != null
                        ? history.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : "";
                createCell(dataRow, colIndex++, dueDate, dataStyle);

                // Ghi chú
                createCell(dataRow, colIndex++, history.getNote(), dataStyle);
            }

            // ===== Row: Tổng cộng =====
            rowIndex++; // Dòng trống
            Row totalRow = sheet.createRow(rowIndex);

            Cell totalLabelCell = totalRow.createCell(0);
            totalLabelCell.setCellValue("TỔNG CỘNG");
            totalLabelCell.setCellStyle(totalLabelStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 3));

            createCurrencyCell(totalRow, 4, totalRoomPrice, totalValueStyle);
            createCurrencyCell(totalRow, 5, totalElectricity, totalValueStyle);
            createCurrencyCell(totalRow, 6, totalWater, totalValueStyle);
            createCurrencyCell(totalRow, 7, totalService, totalValueStyle);
            createCurrencyCell(totalRow, 8, grandTotal, totalValueStyle);

            // ===== Row: Thông tin thống kê =====
            rowIndex += 2;
            Row statsRow1 = sheet.createRow(rowIndex++);
            createCell(statsRow1, 0, "Tổng số giao dịch: " + histories.size(), dataStyle);

            Row statsRow2 = sheet.createRow(rowIndex++);
            createCell(statsRow2, 0, "Tổng doanh thu: " + formatCurrency(grandTotal) + " VNĐ", dataStyle);

            // ===== Auto-size columns =====
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // Đảm bảo cột có chiều rộng tối thiểu
                if (sheet.getColumnWidth(i) < 3000) {
                    sheet.setColumnWidth(i, 3000);
                }
            }

            // Ghi workbook ra output stream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream;

        } catch (IOException e) {
            log.error("Error creating Excel file: {}", e.getMessage());
            throw new RuntimeException("Không thể tạo file Excel: " + e.getMessage());
        }
    }

    // ===== Helper: Tạo cell text =====
    private void createCell(Row row, int colIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        if (value != null) {
            if (value instanceof Number) {
                cell.setCellValue(((Number) value).doubleValue());
            } else {
                cell.setCellValue(value.toString());
            }
        } else {
            cell.setCellValue("");
        }
        cell.setCellStyle(style);
    }

    // ===== Helper: Tạo cell tiền tệ =====
    private void createCurrencyCell(Row row, int colIndex, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        } else {
            cell.setCellValue(0);
        }
        cell.setCellStyle(style);
    }

    // ===== Helper: Format tiền tệ =====
    private String formatCurrency(BigDecimal value) {
        if (value == null) return "0";
        return String.format("%,.0f", value);
    }

    // ===== Styles =====

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle createTotalLabelStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.DARK_RED.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createTotalValueStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.DARK_RED.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0"));
        return style;
    }
}
