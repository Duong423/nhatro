package com.example.nhatro.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.example.nhatro.dto.response.BillResponseDTO;
import com.example.nhatro.service.PdfService;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PdfServiceImpl implements PdfService {

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public ByteArrayOutputStream generateBillPdf(BillResponseDTO bill) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            // Khởi tạo PdfWriter và PdfDocument
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            
            // Sử dụng khổ giấy A5 (148mm x 210mm) - chuẩn hóa đơn
            Document document = new Document(pdfDoc, PageSize.A5);
            document.setMargins(20, 20, 20, 20);
            
            // Load font hỗ trợ tiếng Việt
            PdfFont font = PdfFontFactory.createFont("fonts/ARIAL.TTF", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            PdfFont boldFont = PdfFontFactory.createFont("fonts/ARIALBD.TTF", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            
            document.setFont(font);
            
            // Thêm border cho toàn bộ trang
            float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth();
            float pageHeight = document.getPdfDocument().getDefaultPageSize().getHeight();
            
            // Tạo bảng bọc toàn bộ nội dung với border
            Table mainTable = new Table(1);
            mainTable.setWidth(UnitValue.createPercentValue(100));
            mainTable.setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 2));
            
            // Cell chứa toàn bộ nội dung
            Cell mainCell = new Cell();
            mainCell.setBorder(Border.NO_BORDER);
            mainCell.setPadding(15);
            
            // === HEADER ===
            Paragraph header = new Paragraph("HÓA ĐƠN TIỀN PHÒNG TRỌ")
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setMarginBottom(5);
            mainCell.add(header);
            
            // Thông tin kỳ hóa đơn
            Paragraph period = new Paragraph(String.format("Kỳ: Tháng %d/%d", bill.getBillingMonth(), bill.getBillingYear()))
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15);
            mainCell.add(period);
            
            // === THÔNG TIN HÓA ĐƠN ===
            Table infoTable = new Table(2);
            infoTable.setWidth(UnitValue.createPercentValue(100));
            infoTable.setMarginBottom(10);
            
            addInfoRow(infoTable, "Mã hóa đơn:", "HD" + String.format("%06d", bill.getBillId()), font, boldFont);
            addInfoRow(infoTable, "Mã phòng:", bill.getRoomCode(), font, boldFont);
            addInfoRow(infoTable, "Người thuê:", bill.getTenantName(), font, boldFont);
            addInfoRow(infoTable, "Số điện thoại:", bill.getTenantPhone(), font, boldFont);
            
            if (bill.getDueDate() != null) {
                addInfoRow(infoTable, "Hạn thanh toán:", bill.getDueDate().format(DATE_FORMAT), font, boldFont);
            }
            
            mainCell.add(infoTable);
            
            // === CHI TIẾT THANH TOÁN ===
            Paragraph detailHeader = new Paragraph("CHI TIẾT THANH TOÁN")
                    .setFont(boldFont)
                    .setFontSize(13)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10)
                    .setMarginBottom(10);
            mainCell.add(detailHeader);
            
            // Bảng chi tiết
            Table detailTable = new Table(new float[]{3, 2});
            detailTable.setWidth(UnitValue.createPercentValue(100));
            detailTable.setMarginBottom(10);
            
            // Header bảng
            addTableHeader(detailTable, "Khoản thu", boldFont);
            addTableHeader(detailTable, "Số tiền", boldFont);
            
            // Các khoản thu
            addDetailRow(detailTable, "Tiền phòng", bill.getRoomPrice(), font, boldFont);
            addDetailRow(detailTable, "Tiền điện", bill.getElectricityCost(), font, boldFont);
            addDetailRow(detailTable, "Tiền nước", bill.getWaterCost(), font, boldFont);
            addDetailRow(detailTable, "Dịch vụ khác", bill.getServiceCost(), font, boldFont);
            
            // Tổng cộng
            Cell totalLabelCell = new Cell()
                    .add(new Paragraph("TỔNG CỘNG").setFont(boldFont).setFontSize(12).setBold())
                    .setTextAlignment(TextAlignment.LEFT)
                    .setPadding(8)
                    .setBackgroundColor(new DeviceRgb(240, 240, 240))
                    .setBorder(new SolidBorder(1));
            detailTable.addCell(totalLabelCell);
            
            Cell totalValueCell = new Cell()
                    .add(new Paragraph(formatCurrency(bill.getTotalAmount())).setFont(boldFont).setFontSize(12).setBold())
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setPadding(8)
                    .setBackgroundColor(new DeviceRgb(240, 240, 240))
                    .setBorder(new SolidBorder(1));
            detailTable.addCell(totalValueCell);
            
            mainCell.add(detailTable);
            
            // === TRẠNG THÁI THANH TOÁN ===
            String statusText = "CHƯA THANH TOÁN";
            DeviceRgb statusColor = new DeviceRgb(220, 53, 69); // Red
            
            if ("PAID".equals(bill.getStatus())) {
                statusText = "ĐÃ THANH TOÁN";
                statusColor = new DeviceRgb(40, 167, 69); // Green
            } else if ("OVERDUE".equals(bill.getStatus())) {
                statusText = "QUÁ HẠN";
                statusColor = new DeviceRgb(255, 193, 7); // Yellow
            }
            
            Paragraph statusPara = new Paragraph("Trạng thái: " + statusText)
                    .setFont(boldFont)
                    .setFontSize(11)
                    .setFontColor(statusColor)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10)
                    .setMarginBottom(5);
            mainCell.add(statusPara);
            
            // Thông tin thanh toán (nếu đã thanh toán)
            if (bill.getPaymentDate() != null) {
                Paragraph paymentInfo = new Paragraph("Ngày thanh toán: " + bill.getPaymentDate().format(DATETIME_FORMAT))
                        .setFont(font)
                        .setFontSize(9)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(3);
                mainCell.add(paymentInfo);
                
                if (bill.getPaymentMethod() != null) {
                    Paragraph paymentMethod = new Paragraph("Phương thức: " + bill.getPaymentMethod())
                            .setFont(font)
                            .setFontSize(9)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setMarginBottom(3);
                    mainCell.add(paymentMethod);
                }
                
                if (bill.getTransactionCode() != null) {
                    Paragraph transactionCode = new Paragraph("Mã giao dịch: " + bill.getTransactionCode())
                            .setFont(font)
                            .setFontSize(9)
                            .setTextAlignment(TextAlignment.CENTER);
                    mainCell.add(transactionCode);
                }
            }
            
            // Ghi chú
            if (bill.getNote() != null && !bill.getNote().isEmpty()) {
                Paragraph note = new Paragraph("Ghi chú: " + bill.getNote())
                        .setFont(font)
                        .setFontSize(9)
                        .setItalic()
                        .setMarginTop(10)
                        .setMarginBottom(5);
                mainCell.add(note);
            }
            
            // === FOOTER ===
            Table footerTable = new Table(2);
            footerTable.setWidth(UnitValue.createPercentValue(100));
            footerTable.setMarginTop(20);
            
            // Cột trái - Người thuê
            Cell tenantCell = new Cell()
                    .add(new Paragraph("Người thuê").setFont(boldFont).setFontSize(10).setTextAlignment(TextAlignment.CENTER))
                    .add(new Paragraph("(Ký và ghi rõ họ tên)").setFont(font).setFontSize(8).setTextAlignment(TextAlignment.CENTER).setItalic())
                    .add(new Paragraph("\n\n\n").setFontSize(8))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.CENTER);
            footerTable.addCell(tenantCell);
            
            // Cột phải - Chủ nhà
            Cell ownerCell = new Cell()
                    .add(new Paragraph("Chủ nhà").setFont(boldFont).setFontSize(10).setTextAlignment(TextAlignment.CENTER))
                    .add(new Paragraph("(Ký và ghi rõ họ tên)").setFont(font).setFontSize(8).setTextAlignment(TextAlignment.CENTER).setItalic())
                    .add(new Paragraph("\n\n\n").setFontSize(8))
                    .add(new Paragraph(bill.getOwnerName() != null ? bill.getOwnerName() : "").setFont(font).setFontSize(9).setTextAlignment(TextAlignment.CENTER))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.CENTER);
            footerTable.addCell(ownerCell);
            
            mainCell.add(footerTable);
            
            // Thêm mainCell vào mainTable
            mainTable.addCell(mainCell);
            
            // Thêm mainTable vào document
            document.add(mainTable);
            
            // Đóng document
            document.close();
            
            log.info("PDF generated successfully for bill ID: {}", bill.getBillId());
            
        } catch (IOException e) {
            log.error("Error generating PDF for bill ID: {}", bill.getBillId(), e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
        
        return baos;
    }
    
    private void addInfoRow(Table table, String label, String value, PdfFont font, PdfFont boldFont) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label).setFont(boldFont).setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setPadding(3);
        table.addCell(labelCell);
        
        Cell valueCell = new Cell()
                .add(new Paragraph(value).setFont(font).setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setPadding(3);
        table.addCell(valueCell);
    }
    
    private void addTableHeader(Table table, String text, PdfFont boldFont) {
        Cell headerCell = new Cell()
                .add(new Paragraph(text).setFont(boldFont).setFontSize(11).setBold())
                .setBackgroundColor(new DeviceRgb(52, 58, 64))
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8)
                .setBorder(new SolidBorder(1));
        table.addHeaderCell(headerCell);
    }
    
    private void addDetailRow(Table table, String label, java.math.BigDecimal amount, PdfFont font, PdfFont boldFont) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label).setFont(font).setFontSize(10))
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(6)
                .setBorder(new SolidBorder(1));
        table.addCell(labelCell);
        
        Cell amountCell = new Cell()
                .add(new Paragraph(formatCurrency(amount)).setFont(font).setFontSize(10))
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(6)
                .setBorder(new SolidBorder(1));
        table.addCell(amountCell);
    }
    
    private String formatCurrency(java.math.BigDecimal amount) {
        if (amount == null) {
            return "0 ₫";
        }
        return CURRENCY_FORMAT.format(amount);
    }
}
