# API In Hóa Đơn PDF

## Endpoint
```
GET /api/bills/{billId}/print
```

## Mô tả
API này cho phép in hóa đơn dưới dạng file PDF với định dạng chuẩn hóa đơn (khổ A5), có border và đầy đủ thông tin.

## Phân quyền
- **OWNER**: Có thể in tất cả hóa đơn thuộc sở hữu
- **TENANT**: Chỉ có thể in hóa đơn của mình

## Request

### Headers
```
Authorization: Bearer {token}
```

### Path Parameters
| Tham số | Kiểu | Bắt buộc | Mô tả |
|---------|------|----------|-------|
| billId | Long | Có | ID của hóa đơn cần in |

### Example Request
```http
GET /api/bills/123/print
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Response

### Success Response (200 OK)

**Headers:**
```
Content-Type: application/pdf
Content-Disposition: attachment; filename="HoaDon_P101_Thang11_2024.pdf"
Content-Length: {size}
```

**Body:** Binary PDF file

### Đặc điểm của PDF
1. **Khổ giấy**: A5 (148mm x 210mm) - chuẩn hóa đơn
2. **Border**: Viền đen bao quanh toàn bộ hóa đơn (2px)
3. **Font**: Arial hỗ trợ tiếng Việt
4. **Tên file**: `HoaDon_{RoomCode}_Thang{Month}_{Year}.pdf`

### Nội dung hóa đơn bao gồm:

#### 1. Header
- Tiêu đề: "HÓA ĐƠN TIỀN PHÒNG TRỌ"
- Kỳ hóa đơn: "Kỳ: Tháng {month}/{year}"

#### 2. Thông tin hóa đơn
- Mã hóa đơn
- Mã phòng
- Tên người thuê
- Số điện thoại người thuê
- Hạn thanh toán

#### 3. Chi tiết thanh toán
Bảng chi tiết với các khoản:
- Tiền phòng
- Tiền điện
- Tiền nước
- Dịch vụ khác
- **Tổng cộng** (in đậm, nền xám)

#### 4. Trạng thái thanh toán
- **CHƯA THANH TOÁN** (màu đỏ)
- **ĐÃ THANH TOÁN** (màu xanh lá)
  - Ngày thanh toán
  - Phương thức thanh toán
  - Mã giao dịch (nếu có)
- **QUÁ HẠN** (màu vàng)

#### 5. Ghi chú
Hiển thị ghi chú nếu có

#### 6. Footer
Chữ ký của:
- Người thuê (trái)
- Chủ nhà (phải) - có in sẵn tên

### Error Responses

#### 404 Not Found
Hóa đơn không tồn tại hoặc không có quyền truy cập
```json
{
  "code": 404,
  "message": "Bill not found with ID: 123",
  "result": null
}
```

#### 401 Unauthorized
Chưa đăng nhập
```json
{
  "code": 401,
  "message": "Unauthorized",
  "result": null
}
```

#### 403 Forbidden
Không có quyền truy cập hóa đơn này
```json
{
  "code": 403,
  "message": "Access denied",
  "result": null
}
```

#### 500 Internal Server Error
Lỗi khi tạo PDF
```json
{
  "code": 500,
  "message": "Failed to generate PDF",
  "result": null
}
```

## Ví dụ sử dụng

### 1. Sử dụng cURL
```bash
curl -X GET "http://localhost:8080/api/bills/123/print" \
  -H "Authorization: Bearer {token}" \
  --output hoadon.pdf
```

### 2. Sử dụng JavaScript (Fetch API)
```javascript
async function downloadBillPdf(billId) {
  const response = await fetch(`/api/bills/${billId}/print`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  if (response.ok) {
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `HoaDon_${billId}.pdf`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  }
}
```

### 3. Sử dụng HTML (Link trực tiếp)
```html
<a href="/api/bills/123/print" 
   target="_blank"
   class="btn btn-primary">
  <i class="fas fa-print"></i> In hóa đơn
</a>
```

### 4. Sử dụng Axios
```javascript
axios({
  method: 'GET',
  url: `/api/bills/${billId}/print`,
  responseType: 'blob',
  headers: {
    'Authorization': `Bearer ${token}`
  }
}).then(response => {
  const url = window.URL.createObjectURL(new Blob([response.data]));
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', `HoaDon_${billId}.pdf`);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
});
```

## Lưu ý kỹ thuật

1. **Font chữ**: Sử dụng Arial từ thư mục `resources/fonts/`
2. **Encoding**: UTF-8 để hiển thị đúng tiếng Việt
3. **Định dạng tiền tệ**: VNĐ (₫)
4. **Định dạng ngày**: dd/MM/yyyy
5. **Định dạng ngày giờ**: dd/MM/yyyy HH:mm:ss
6. **Response Type**: `application/pdf`
7. **Download**: File được download tự động với tên có ý nghĩa

## Dependencies
```gradle
// PDF Generation
implementation 'com.itextpdf:itext7-core:7.2.5'
implementation 'com.itextpdf:html2pdf:4.0.5'
```

## Cấu trúc code

### Service Layer
- **Interface**: `PdfService`
- **Implementation**: `PdfServiceImpl`
- **Method**: `ByteArrayOutputStream generateBillPdf(BillResponseDTO bill)`

### Controller Layer
- **Controller**: `BillController`
- **Endpoint**: `GET /{billId}/print`
- **Method**: `printBillPdf(Long billId)`

## Testing

### Test thủ công
1. Tạo một hóa đơn mới hoặc sử dụng hóa đơn có sẵn
2. Lấy token đăng nhập (OWNER hoặc TENANT)
3. Gọi API với billId
4. Kiểm tra file PDF được download
5. Mở PDF và xác nhận:
   - Border hiển thị đúng
   - Font tiếng Việt hiển thị chính xác
   - Các thông tin đầy đủ và chính xác
   - Layout đẹp, dễ đọc

### Test cases
1. ✅ Owner in hóa đơn của mình
2. ✅ Tenant in hóa đơn của mình
3. ❌ Tenant in hóa đơn của người khác (403)
4. ❌ Hóa đơn không tồn tại (404)
5. ✅ Hóa đơn đã thanh toán (hiển thị thông tin thanh toán)
6. ✅ Hóa đơn chưa thanh toán
7. ✅ Hóa đơn quá hạn

## Tích hợp Frontend

### React Component Example
```jsx
import React from 'react';
import { Button } from 'react-bootstrap';
import { FaPrint } from 'react-icons/fa';

const BillPrintButton = ({ billId }) => {
  const handlePrint = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`/api/bills/${billId}/print`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      
      if (response.ok) {
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `HoaDon_${billId}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
      }
    } catch (error) {
      console.error('Error printing bill:', error);
      alert('Không thể in hóa đơn');
    }
  };

  return (
    <Button variant="primary" onClick={handlePrint}>
      <FaPrint /> In hóa đơn
    </Button>
  );
};

export default BillPrintButton;
```
