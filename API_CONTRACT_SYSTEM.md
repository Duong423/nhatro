# API CONTRACT SYSTEM - HỆ THỐNG HỢP ĐỒNG THUÊ NHÀ TRỌ

## Tổng quan

Hệ thống quản lý hợp đồng thuê nhà trọ, tích hợp với hệ thống Booking để tạo hợp đồng chính thức sau khi booking được xác nhận.

## Luồng hoạt động

1. **Booking được xác nhận** (status: CONFIRMED)
2. **Owner tạo hợp đồng** từ booking (Contract status: PENDING)
3. **Tenant/Owner ký hợp đồng** (Contract status: ACTIVE, Booking status: COMPLETED)
4. **Khi hợp đồng kết thúc** (Contract status: TERMINATED, Hostel status: AVAILABLE)

---

## 1. TẠO HỢP ĐỒNG (OWNER)

**Endpoint:** `POST /api/contracts/create`

**Authorization:** Owner (có quyền OWNER hoặc ADMIN)

**Điều kiện:** 
- Booking phải ở trạng thái CONFIRMED
- Chưa có hợp đồng cho booking này
- User hiện tại phải là owner của hostel

### Request Body

```json
{
  "bookingId": 1,
  "startDate": "2026-02-01",
  "endDate": "2027-02-01",
  "monthlyRent": 3000000,
  "electricityCostPerUnit": 3500,
  "waterCostPerUnit": 15000,
  "serviceFee": 200000,
  "paymentCycle": "MONTHLY",
  "numberOfTenants": 2,
  "terms": "1. Thanh toán tiền thuê vào ngày 5 hàng tháng\n2. Không được chuyển nhượng phòng\n3. Giữ gìn vệ sinh chung\n4. Báo trước 1 tháng nếu muốn chấm dứt hợp đồng",
  "notes": "Khách hàng uy tín"
}
```

### Request Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| bookingId | Long | ✅ | ID của booking đã confirmed |
| startDate | LocalDate | ✅ | Ngày bắt đầu hợp đồng (yyyy-MM-dd) |
| endDate | LocalDate | ✅ | Ngày kết thúc hợp đồng (phải sau startDate) |
| monthlyRent | Double | ✅ | Giá thuê hàng tháng (VNĐ) |
| electricityCostPerUnit | Double | ❌ | Giá điện/kWh (VNĐ) |
| waterCostPerUnit | Double | ❌ | Giá nước/m³ (VNĐ) |
| serviceFee | Double | ❌ | Phí dịch vụ khác (internet, rác, vệ sinh...) |
| paymentCycle | String | ❌ | Chu kỳ thanh toán: MONTHLY/QUARTERLY/YEARLY (default: MONTHLY) |
| numberOfTenants | Integer | ❌ | Số người ở (default: 1) |
| terms | String | ❌ | Điều khoản hợp đồng (text dài) |
| notes | String | ❌ | Ghi chú |

### Response Success (201)

```json
{
  "code": 201,
  "message": "Contract created successfully",
  "result": {
    "contractId": 1,
    "bookingId": 1,
    "tenantId": 2,
    "tenantName": "Nguyễn Văn A",
    "tenantPhone": "0123456789",
    "tenantEmail": "nguyenvana@gmail.com",
    "landlordId": 3,
    "landlordName": "Trần Thị B",
    "landlordPhone": "0987654321",
    "hostelId": 1,
    "hostelName": "Phòng trọ ABC",
    "hostelAddress": "123 Đường X, Quận Y, TP.HCM",
    "startDate": "2026-02-01",
    "endDate": "2027-02-01",
    "monthlyRent": 3000000.00,
    "depositAmount": 5000000.00,
    "electricityCostPerUnit": 3500.00,
    "waterCostPerUnit": 15000.00,
    "serviceFee": 200000.00,
    "paymentCycle": "MONTHLY",
    "numberOfTenants": 2,
    "terms": "1. Thanh toán tiền thuê vào ngày 5 hàng tháng...",
    "signedDate": null,
    "status": "PENDING",
    "notes": "Khách hàng uy tín",
    "createdAt": "2026-01-28T14:30:00",
    "updatedAt": "2026-01-28T14:30:00"
  }
}
```

### Response Error (400)

```json
{
  "code": 400,
  "message": "Error creating contract: Booking must be CONFIRMED to create contract. Current status: PENDING",
  "result": null
}
```

---

## 2. XEM CHI TIẾT HỢP ĐỒNG

**Endpoint:** `GET /api/contracts/{contractId}`

**Authorization:** Authenticated (Tenant hoặc Landlord của hợp đồng)

### Response Success (200)

```json
{
  "code": 200,
  "message": "Contract retrieved successfully",
  "result": {
    "contractId": 1,
    "bookingId": 1,
    "tenantId": 2,
    "tenantName": "Nguyễn Văn A",
    "tenantPhone": "0123456789",
    "tenantEmail": "nguyenvana@gmail.com",
    "landlordId": 3,
    "landlordName": "Trần Thị B",
    "landlordPhone": "0987654321",
    "hostelId": 1,
    "hostelName": "Phòng trọ ABC",
    "hostelAddress": "123 Đường X, Quận Y, TP.HCM",
    "startDate": "2026-02-01",
    "endDate": "2027-02-01",
    "monthlyRent": 3000000.00,
    "depositAmount": 5000000.00,
    "electricityCostPerUnit": 3500.00,
    "waterCostPerUnit": 15000.00,
    "serviceFee": 200000.00,
    "paymentCycle": "MONTHLY",
    "numberOfTenants": 2,
    "terms": "1. Thanh toán tiền thuê vào ngày 5 hàng tháng...",
    "signedDate": "2026-01-29",
    "status": "ACTIVE",
    "notes": "Khách hàng uy tín",
    "createdAt": "2026-01-28T14:30:00",
    "updatedAt": "2026-01-29T10:15:00"
  }
}
```

---

## 3. LẤY HỢP ĐỒNG THEO BOOKING

**Endpoint:** `GET /api/contracts/booking/{bookingId}`

**Authorization:** Authenticated

**Use case:** Kiểm tra xem một booking đã có hợp đồng chưa

### Response Success (200)

```json
{
  "code": 200,
  "message": "Contract retrieved successfully",
  "result": { /* Contract object */ }
}
```

### Response Error (404)

```json
{
  "code": 404,
  "message": "Error retrieving contract: Contract not found for booking ID: 1",
  "result": null
}
```

---

## 4. DANH SÁCH HỢP ĐỒNG CỦA KHÁCH THUÊ

**Endpoint:** `GET /api/contracts/my-contracts`

**Authorization:** Authenticated (Tenant)

**Use case:** Khách thuê xem tất cả hợp đồng của mình

### Response Success (200)

```json
{
  "code": 200,
  "message": "Contracts retrieved successfully",
  "result": [
    {
      "contractId": 1,
      "bookingId": 1,
      "tenantId": 2,
      "tenantName": "Nguyễn Văn A",
      "landlordName": "Trần Thị B",
      "hostelName": "Phòng trọ ABC",
      "hostelAddress": "123 Đường X",
      "startDate": "2026-02-01",
      "endDate": "2027-02-01",
      "monthlyRent": 3000000.00,
      "status": "ACTIVE"
      /* ... other fields */
    },
    {
      "contractId": 2,
      "status": "TERMINATED"
      /* ... */
    }
  ]
}
```

---

## 5. DANH SÁCH HỢP ĐỒNG CỦA CHỦ NHÀ

**Endpoint:** `GET /api/contracts/owner/all`

**Authorization:** Owner (OWNER hoặc ADMIN)

**Use case:** Chủ nhà xem tất cả hợp đồng của các phòng trọ mình quản lý

### Response Success (200)

```json
{
  "code": 200,
  "message": "Contracts retrieved successfully",
  "result": [
    {
      "contractId": 1,
      "bookingId": 1,
      "tenantName": "Nguyễn Văn A",
      "tenantPhone": "0123456789",
      "hostelName": "Phòng trọ ABC",
      "startDate": "2026-02-01",
      "endDate": "2027-02-01",
      "monthlyRent": 3000000.00,
      "status": "ACTIVE"
      /* ... */
    },
    {
      "contractId": 3,
      "tenantName": "Lê Văn C",
      "hostelName": "Phòng trọ XYZ",
      "status": "PENDING"
      /* ... */
    }
  ]
}
```

---

## 6. KÝ HỢP ĐỒNG

**Endpoint:** `PUT /api/contracts/{contractId}/sign`

**Authorization:** Authenticated (Tenant hoặc Landlord)

**Điều kiện:** 
- Hợp đồng phải ở trạng thái PENDING
- Chỉ tenant hoặc landlord mới được ký

**Hiệu ứng:**
- ✅ Contract status → ACTIVE
- ✅ Booking status → COMPLETED
- ✅ Set signedDate = ngày hiện tại

### Response Success (200)

```json
{
  "code": 200,
  "message": "Contract signed successfully",
  "result": {
    "contractId": 1,
    "status": "ACTIVE",
    "signedDate": "2026-01-29",
    /* ... other fields */
  }
}
```

### Response Error (400)

```json
{
  "code": 400,
  "message": "Error signing contract: Only PENDING contracts can be signed",
  "result": null
}
```

---

## 7. CHẤM DỨT HỢP ĐỒNG

**Endpoint:** `PUT /api/contracts/{contractId}/terminate`

**Authorization:** Owner (chỉ landlord mới được chấm dứt)

**Query Parameters:**
- `reason` (optional): Lý do chấm dứt hợp đồng

**Điều kiện:**
- Hợp đồng chưa bị TERMINATED
- Chỉ landlord mới có quyền

**Hiệu ứng:**
- ✅ Contract status → TERMINATED
- ✅ Hostel status → AVAILABLE (phòng có thể cho thuê lại)
- ✅ Lưu lý do vào notes

### Request

```
PUT /api/contracts/1/terminate?reason=Khách thuê vi phạm hợp đồng
```

### Response Success (200)

```json
{
  "code": 200,
  "message": "Contract terminated successfully",
  "result": {
    "contractId": 1,
    "status": "TERMINATED",
    "notes": "Khách hàng uy tín\nTerminated: Khách thuê vi phạm hợp đồng",
    /* ... other fields */
  }
}
```

### Response Error (400)

```json
{
  "code": 400,
  "message": "Error terminating contract: Only landlord can terminate contract",
  "result": null
}
```

---

## Contract Status Flow

```
PENDING → ACTIVE → TERMINATED/EXPIRED
   ↓         ↓           ↓
(Tạo mới) (Ký HĐ)  (Kết thúc)
          Booking      Hostel
          COMPLETED   AVAILABLE
```

## Trạng thái hợp đồng

| Status | Description | Action |
|--------|-------------|--------|
| **PENDING** | Chờ ký | Vừa tạo, chưa có chữ ký |
| **ACTIVE** | Đang hoạt động | Đã ký, đang trong thời hạn |
| **EXPIRED** | Hết hạn | Hợp đồng đã hết hạn tự nhiên |
| **TERMINATED** | Đã chấm dứt | Bị chấm dứt trước hạn |

---

## Validation Rules

### Tạo hợp đồng:
- ✅ Booking phải ở trạng thái CONFIRMED
- ✅ Chưa có hợp đồng cho booking này
- ✅ endDate phải sau startDate
- ✅ monthlyRent > 0
- ✅ Chỉ owner của hostel mới tạo được

### Ký hợp đồng:
- ✅ Hợp đồng phải PENDING
- ✅ Chỉ tenant hoặc landlord được ký

### Chấm dứt hợp đồng:
- ✅ Hợp đồng chưa TERMINATED
- ✅ Chỉ landlord được chấm dứt

---

## Error Codes

| Code | Message | Cause |
|------|---------|-------|
| 400 | Booking must be CONFIRMED | Booking chưa được xác nhận |
| 400 | Contract already exists | Booking đã có hợp đồng |
| 400 | End date must be after start date | Ngày kết thúc không hợp lệ |
| 400 | Only PENDING contracts can be signed | Hợp đồng đã được ký rồi |
| 400 | Only landlord can terminate | Không có quyền chấm dứt |
| 404 | Contract not found | Không tìm thấy hợp đồng |
| 403 | You don't have permission | Không có quyền truy cập |

---

## Notes cho Frontend

### 1. Hiển thị Contract Status:
```javascript
const statusColors = {
  PENDING: 'yellow',    // Chờ ký
  ACTIVE: 'green',      // Đang hoạt động
  EXPIRED: 'gray',      // Hết hạn
  TERMINATED: 'red'     // Đã chấm dứt
};
```

### 2. Disable button "Ký hợp đồng" nếu:
- Status không phải PENDING
- User không phải tenant hoặc landlord

### 3. Chỉ hiển thị button "Chấm dứt" nếu:
- User là landlord
- Status không phải TERMINATED

### 4. Format số tiền:
```javascript
const formatCurrency = (amount) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount);
};
```

### 5. Kiểm tra hợp đồng sắp hết hạn:
```javascript
const isExpiringSoon = (endDate) => {
  const daysLeft = Math.ceil((new Date(endDate) - new Date()) / (1000 * 60 * 60 * 24));
  return daysLeft <= 30 && daysLeft > 0;
};
```

---

## Integration Checklist

- [ ] Tạo form tạo hợp đồng với validation
- [ ] Hiển thị danh sách hợp đồng (tenant view)
- [ ] Hiển thị danh sách hợp đồng (owner view)
- [ ] Chi tiết hợp đồng với PDF export
- [ ] Button ký hợp đồng (với confirm dialog)
- [ ] Button chấm dứt hợp đồng (với reason input)
- [ ] Filter theo status (PENDING/ACTIVE/TERMINATED)
- [ ] Search theo tên tenant/hostel
- [ ] Notification khi hợp đồng sắp hết hạn
- [ ] Link từ booking detail → contract detail
