package com.example.nhatro.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import com.example.nhatro.dto.response.ContractResponseDTO;

@Service
public class ContractPdfGenerator {

    private static final float MARGIN = 50;
    // Tăng khoảng cách dòng để tránh bị đè chữ
    private static final float LEADING = 20;

    public byte[] generateContractPdf(ContractResponseDTO dto) throws IOException {
        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDType0Font uFont = loadUnicodeFontIfAvailable(doc);
            // If content contains Unicode (Vietnamese) but no Unicode font found, throw clear error
            ensureUnicodeSupportOrThrow(uFont, dto);
            PDFont titleFont = uFont != null ? uFont : PDType1Font.HELVETICA_BOLD;
            PDFont normalFont = uFont != null ? uFont : PDType1Font.HELVETICA;
            // Use ContentWriter to manage page flow and avoid overlap
            ContentWriter writer = new ContentWriter(doc, page, titleFont, normalFont);

            String date = dto.getSignedDate() != null ? dto.getSignedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "____/__/__";

            writer.drawCenterText(16, "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM");
            writer.drawCenterText(14, "Độc lập - Tự do - Hạnh phúc");
            writer.skip(8);
            writer.drawCenterText(18, "HỢP ĐỒNG THUÊ TRỌ");
            writer.skip(14);

            writer.writeParagraph(11, "Hôm nay, ngày " + date + ", tại: " + safe(dto.getHostelAddress()));
            writer.skip(6);

            writer.writeSectionTitle(12, "BÊN CHO THUÊ (BÊN A)");
            writer.writeKeyValues(11, new String[][]{
                    {"Họ và tên", safe(dto.getOwnerName())},
                    {"Số điện thoại", safe(dto.getPhoneNumberOwner())},
                    {"Địa chỉ", safe(dto.getHostelAddress())}
            });

            writer.skip(4);

            writer.writeSectionTitle(12, "BÊN THUÊ (BÊN B)");
            writer.writeKeyValues(11, new String[][]{
                    {"Họ và tên", safe(dto.getTenantName())},
                    {"Số điện thoại", safe(dto.getPhoneNumberTenant())},
                    {"Email", safe(dto.getTenantEmail())}
            });

            writer.skip(6);

            writer.writeSectionTitle(12, "ĐIỀU 1. THÔNG TIN NHÀ/PHÒNG");
            writer.writeParagraph(11, "Địa chỉ: " + safe(dto.getHostelAddress()) + "\nDiện tích: " + safe(dto.getHostelArea()) + " m2\nTiện nghi: " + safe(dto.getHostelAmenities()));

            writer.skip(4);

            writer.writeSectionTitle(12, "ĐIỀU 2. THỜI HẠN VÀ GIÁ THUÊ");
            writer.writeJustifiedParagraph(11, "Thời hạn thuê: từ " + (dto.getStartDate() != null ? dto.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "____/__/__")
                    + " đến " + (dto.getEndDate() != null ? dto.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "____/__/__") + ".\n"
                    + "Giá thuê: " + safe(dto.getMonthlyRent()) + " VND/tháng. Chu kỳ thanh toán: " + safe(dto.getPaymentCycle()) + ".");

            writer.skip(4);

            writer.writeSectionTitle(12, "ĐIỀU 3. TIỀN ĐẶT CỌC VÀ ĐIỆN - NƯỚC");
            writer.writeJustifiedParagraph(12, "Tiền đặt cọc: " + safe(dto.getDepositAmount()) + " VND. "
                    + "Chi phí điện, nước tính theo đồng hồ: điện " + safe(dto.getElectricityCostPerUnit()) + " VND/đơn vị, nước " + safe(dto.getWaterCostPerUnit()) + " VND/đơn vị. "
                    + "Các dịch vụ khác: " + (dto.getServiceFee() != null ? dto.getServiceFee().toString() : "thỏa thuận"));

            writer.skip(12);

            writer.writeSectionTitle(12, "ĐIỀU 4. QUYỀN VÀ TRÁCH NHIỆM");
            writer.writeParagraph(11, "1. Bên A bàn giao phòng đúng như mô tả và chịu trách nhiệm các hư hỏng lớn do hao mòn.\n"
                    + "2. Bên B sử dụng đúng mục đích, bảo quản tài sản, không làm thay đổi kết cấu phòng.\n"
                    + "3. Vi phạm các điều khoản có thể dẫn đến chấm dứt hợp đồng và bồi thường theo quy định.");

            writer.skip(6);

            writer.writeSectionTitle(12, "ĐIỀU 5. CHẤM DỨT HỢP ĐỒNG VÀ GIẢI QUYẾT TRANH CHẤP");
            writer.writeJustifiedParagraph(12, "Hợp đồng kết thúc khi hết thời hạn, hai bên thỏa thuận chấm dứt, hoặc theo quyết định của tòa án. Mọi tranh chấp được giải quyết thông qua thương lượng, nếu không được, sẽ đưa ra tòa án có thẩm quyền.");

            writer.skip(12);

            // Signature area — no lines, only labels
            writer.skip(30);
            writer.drawSignatureLabels(11, safe(dto.getOwnerName()), safe(dto.getTenantName()), date);

            // close writer
            writer.close();

            doc.save(baos);
            return baos.toByteArray();
        }
    }

    // ContentWriter handles page breaks and writes text safely
    private class ContentWriter {
        private final PDDocument doc;
        private PDPage currentPage;
        private PDPageContentStream cs;
        private final float pageWidth;
        private float y;
        private final PDFont titleFont;
        private final PDFont normalFont;

        ContentWriter(PDDocument doc, PDPage startPage, PDFont titleFont, PDFont normalFont) throws IOException {
            this.doc = doc;
            this.currentPage = startPage;
            this.cs = new PDPageContentStream(doc, currentPage);
            this.pageWidth = currentPage.getMediaBox().getWidth();
            this.y = currentPage.getMediaBox().getHeight() - MARGIN;
            this.titleFont = titleFont;
            this.normalFont = normalFont;
        }

        void ensureSpace(float needed) throws IOException {
            if (y - needed < MARGIN) {
                cs.close();
                currentPage = new PDPage(PDRectangle.A4);
                doc.addPage(currentPage);
                cs = new PDPageContentStream(doc, currentPage);
                y = currentPage.getMediaBox().getHeight() - MARGIN;
            }
        }

        void skip(float points) throws IOException {
            y -= points;
            ensureSpace(0);
        }

        void drawCenterText(float size, String text) throws IOException {
            ensureSpace(size + 4);
            float textWidth = titleFont.getStringWidth(text) / 1000 * size;
            float x = (pageWidth - textWidth) / 2;
            try {
                cs.beginText();
                cs.setFont(titleFont, size);
                cs.newLineAtOffset(x, y);
                cs.showText(text);
            } finally {
                cs.endText();
            }
            y -= size + 4;
        }

        void writeSectionTitle(float size, String title) throws IOException {
            ensureSpace(size + 6);
            try {
                cs.beginText();
                cs.setFont(titleFont, size);
                cs.newLineAtOffset(MARGIN, y);
                cs.showText(title);
            } finally {
                cs.endText();
            }
            y -= (size + 6);
        }

        void writeKeyValues(float size, String[][] items) throws IOException {
            for (String[] kv : items) {
                ensureSpace(LEADING);
                String line = kv[0] + ": " + kv[1];
                try {
                    cs.beginText();
                    cs.setFont(normalFont, size);
                    cs.newLineAtOffset(MARGIN, y);
                    cs.showText(line);
                } finally {
                    cs.endText();
                }
                y -= LEADING;
            }
        }

        void writeParagraph(float size, String text) throws IOException {
            for (String line : text.split("\\n")) {
                List<String> lines = wrapText(normalFont, size, line, pageWidth - 2 * MARGIN);
                for (String l : lines) {
                    ensureSpace(LEADING);
                    try {
                        cs.beginText();
                        cs.setFont(normalFont, size);
                        cs.newLineAtOffset(MARGIN, y);
                        cs.showText(l);
                    } finally {
                        cs.endText();
                    }
                    y -= LEADING;
                }
            }
        }

        void writeJustifiedParagraph(float size, String text) throws IOException {
            for (String p : text.split("\\n")) {
                List<String> lines = wrapText(normalFont, size, p, pageWidth - 2 * MARGIN);
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    ensureSpace(LEADING);
                    try {
                        cs.beginText();
                        cs.setFont(normalFont, size);
                        cs.newLineAtOffset(MARGIN, y);
                        if (i == lines.size() - 1 || !line.contains(" ")) {
                            cs.showText(line);
                        } else {
                            justifyLine(cs, normalFont, size, pageWidth - 2 * MARGIN, line);
                        }
                    } finally {
                        cs.endText();
                    }
                    y -= LEADING;
                }
            }
        }

        void drawSignatureLabels(float size, String ownerName, String tenantName, String date) throws IOException {
            ensureSpace(80);
            // Left
            try {
                cs.beginText();
                cs.setFont(normalFont, size);
                cs.newLineAtOffset(MARGIN + 5, y);
                cs.showText("Ký tên, ghi rõ họ tên");
            } finally {
                cs.endText();
            }

            try {
                cs.beginText();
                cs.setFont(normalFont, size);
                cs.newLineAtOffset(MARGIN + (pageWidth - 2 * MARGIN) / 2 + 25, y);
                cs.showText("Ký tên, ghi rõ họ tên");
            } finally {
                cs.endText();
            }

            float nameY = y - 40;
            try {
                cs.beginText();
                cs.setFont(normalFont, size);
                cs.newLineAtOffset(MARGIN + 5, nameY);
                // cs.showText("(In hoa) " + ownerName);
            } finally {
                cs.endText();
            }

            try {
                cs.beginText();
                cs.setFont(normalFont, size);
                cs.newLineAtOffset(MARGIN + (pageWidth - 2 * MARGIN) / 2 + 25, nameY);
                // cs.showText("(In hoa) " + tenantName);
            } finally {
                cs.endText();
            }

            try {
                cs.beginText();
                cs.setFont(normalFont, size);
                cs.newLineAtOffset(MARGIN + (pageWidth - 2 * MARGIN) / 2 + 25, nameY - 20);
                // cs.showText("Ngày: " + date);
            } finally {
                cs.endText();
            }

            y = nameY - 40;
        }

        void close() throws IOException {
            if (cs != null) cs.close();
        }
    }

    private float writeParagraph(PDPageContentStream cs, PDFont font, float size, float x, float y, float maxWidth, String text) throws IOException {
        for (String line : text.split("\\n")) {
            List<String> lines = wrapText(font, size, line, maxWidth);
            for (String l : lines) {
                try {
                    cs.beginText();
                    cs.setFont(font, size);
                    cs.newLineAtOffset(x, y);
                    cs.showText(l);
                } finally {
                    cs.endText();
                }
                y -= LEADING;
            }
        }
        return y;
    }

    private float writeJustifiedParagraph(PDPageContentStream cs, PDFont font, float size, float x, float y, float maxWidth, String text) throws IOException {
        for (String p : text.split("\\n")) {
            List<String> lines = wrapText(font, size, p, maxWidth);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                try {
                    cs.beginText();
                    cs.setFont(font, size);
                    cs.newLineAtOffset(x, y);
                    if (i == lines.size() - 1 || !line.contains(" ")) {
                        cs.showText(line);
                    } else {
                        justifyLine(cs, font, size, maxWidth, line);
                    }
                } finally {
                    cs.endText();
                }
                y -= LEADING;
            }
        }
        return y;
    }

    private void justifyLine(PDPageContentStream cs, PDFont font, float size, float maxWidth, String line) throws IOException {
        String[] words = line.split(" ");
        float wordsWidth = 0;
        for (String w : words) {
            wordsWidth += font.getStringWidth(w) / 1000 * size;
        }
        
        // Nếu chỉ có 1 từ thì không cần giãn dòng
        if (words.length <= 1) {
            cs.showText(line);
            return;
        }

        float space = (maxWidth - wordsWidth) / (words.length - 1);

        for (int i = 0; i < words.length; i++) {
            cs.showText(words[i]);
            if (i < words.length - 1) {
                // SỬA LỖI TẠI ĐÂY:
                // Cần cộng thêm chiều rộng của từ vừa viết để dời điểm bắt đầu (anchor) của từ tiếp theo đi đúng vị trí
                float currentWordWidth = font.getStringWidth(words[i]) / 1000 * size;
                cs.newLineAtOffset(currentWordWidth + space, 0);
            }
        }
    }

    private List<String> wrapText(PDFont font, float size, String text, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : text.split("\\s+")) {
            // If the single word is wider than maxWidth, break it into smaller pieces
            float wordWidth = font.getStringWidth(word) / 1000 * size;
            if (wordWidth > maxWidth) {
                // flush current line first
                if (line.length() > 0) {
                    lines.add(line.toString());
                    line = new StringBuilder();
                }
                // break the long word into chunks that fit
                int start = 0;
                while (start < word.length()) {
                    int end = findBreakIndex(font, size, word, start, maxWidth);
                    String part = word.substring(start, end);
                    lines.add(part);
                    start = end;
                }
            } else {
                String test = line.length() == 0 ? word : line + " " + word;
                float width = font.getStringWidth(test) / 1000 * size;
                if (width > maxWidth) {
                    lines.add(line.toString());
                    line = new StringBuilder(word);
                } else {
                    if (line.length() > 0) line.append(" ");
                    line.append(word);
                }
            }
        }
        if (line.length() > 0) lines.add(line.toString());
        return lines;
    }

    private int findBreakIndex(PDFont font, float size, String word, int start, float maxWidth) throws IOException {
        // Find largest end index such that substring [start, end) fits in maxWidth
        int end = start + 1;
        for (; end <= word.length(); end++) {
            String part = word.substring(start, end);
            float w = font.getStringWidth(part) / 1000 * size;
            if (w > maxWidth) {
                return Math.max(start + 1, end - 1);
            }
        }
        return word.length();
    }

    private void drawSignatureBox(PDPageContentStream cs, PDFont font, float fontSize, float x, float yTop, float width) throws IOException {
        float lineY = yTop - 20;
        // Đã bỏ đường gạch ngang theo yêu cầu (không vẽ line nữa)

        // Place label using provided font to support Unicode
        try {
            cs.beginText();
            cs.setFont(font, fontSize);
            cs.newLineAtOffset(x + 5, lineY + 12);
            cs.showText("Ký tên, ghi rõ họ tên");
        } finally {
            cs.endText();
        }
    }

    private String safe(Object o) {
        return o == null ? "" : o.toString();
    }

    private PDType0Font loadUnicodeFontIfAvailable(PDDocument doc) {
        // Tên file font bạn đã để trong src/main/resources/fonts/
        // Ví dụ: "Arial.ttf" hoặc "Roboto-Regular.ttf"
        String fontName = "ARIAL.TTF"; 
        String fontPath = "/fonts/" + fontName;

        try (InputStream is = getClass().getResourceAsStream(fontPath)) {
            if (is != null) {
                // Load font từ Resource
                return PDType0Font.load(doc, is, true);
            } else {
                // Log ra nếu không tìm thấy file để dễ debug
                System.err.println("Không tìm thấy file font tại đường dẫn: " + fontPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Nếu không load được font tiếng Việt, trả về null (sẽ bị lỗi font nếu có ký tự có dấu)
        return null;
    }
    private void ensureUnicodeSupportOrThrow(PDType0Font uFont, ContractResponseDTO dto) {
        if (uFont != null) return;
        String combined = safe(dto.getOwnerName()) + safe(dto.getTenantName()) + safe(dto.getHostelName()) + safe(dto.getHostelAddress());
        for (int i = 0; i < combined.length(); i++) {
            if (combined.codePointAt(i) > 255) {
                throw new IllegalStateException("No Unicode TTF font found on the system or in resources. Please add a TTF supporting Vietnamese to src/main/resources/fonts/ (e.g., DejaVuSans.ttf)");
            }
        }
    }
}