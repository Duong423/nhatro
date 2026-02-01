# API Hệ thống Thanh toán Hóa đơn

## Tổng quan
Hệ thống quản lý hóa đơn hàng tháng cho nhà trọ với tính năng tự động tạo hóa đơn và xác nhận thanh toán thủ công.

## Tính năng chính
- ✅ Tự động tạo hóa đơn vào ngày 25 hàng tháng
- ✅ Xác nhận thanh toán thủ công
- ✅ Quản lý hóa đơn cho Owner và Tenant
- ✅ Theo dõi lịch sử thanh toán

---

## API Endpoints

### 1. Tạo hóa đơn thủ công (Owner)
**POST** `/api/bills`

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "contractId": 1,
  "billingMonth": 2,
  "billingYear": 2026,
  "note": "Hóa đơn tháng 2/2026"
}
```

**Hoặc sử dụng roomCode thay vì contractId:**
```json
{
  "roomCode": "P101",
  "billingMonth": 2,
  "billingYear": 2026,
  "note": "Hóa đơn tháng 2/2026"
}
```

**Lưu ý:** Tất cả thông tin giá cả (roomPrice, electricityCost, waterCost, serviceCost), roomCode, tenant, owner sẽ **tự động lấy từ Contract**. Bạn chỉ cần truyền `contractId` hoặc `roomCode` + tháng/năm.

**Response:** `201 Created`
```json
{
  "billId": 1,
  "contractId": 1,
  "roomCode": "P101",
  "billingMonth": 2,
  "billingYear": 2026,
  "roomPrice": 3000000,
  "electricityCost": 0,
  "waterCost": 0,
  "serviceCost": 100000,
  "totalAmount": 3100000,
  "status": "PENDING",
  "dueDate": "2026-03-05",
  "paymentDate": null,
  "note": "Hóa đơn tháng 2/2026",
  "paymentMethod": null,
  "transactionCode": null,
  "tenantId": 5,
  "tenantName": "Nguyễn Văn A",
  "tenantPhone": "0901234567",
  "ownerId": 3,
  "ownerName": "Trần Thị B",
  "ownerPhone": "0912345678",
  "createdAt": "2026-02-01T10:00:00",
  "updatedAt": "2026-02-01T10:00:00"
}
```

---

### 2. Lấy chi tiết hóa đơn (Owner & Tenant)
**GET** `/api/bills/{billId}`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:** `200 OK`
```json
{
  "billId": 1,
  "contractId": 1,
  "roomCode": "P101",
  "billingMonth": 2,
  "billingYear": 2026,
  "totalAmount": 3350000,
  "status": "PENDING",
  ...
}
```

---

### 3. Lấy danh sách hóa đơn của Owner
**GET** `/api/bills/owner`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:** `200 OK`
```json
[
  {
    "billId": 1,
    "roomCode": "P101",
    "totalAmount": 3350000,
    "status": "PENDING",
    ...
  },
  {
    "billId": 2,
    "roomCode": "P102",
    "totalAmount": 2800000,
    "status": "PAID",
    ...
  }
]
```

---

### 4. Lấy danh sách hóa đơn của Tenant
**GET** `/api/bills/tenant`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:** `200 OK`
```json
[
  {
    "billId": 1,
    "roomCode": "P101",
    "totalAmount": 3350000,
    "status": "PENDING",
    "dueDate": "2026-03-05",
    ...
  }
]
```

---

### 5. Lấy hóa đơn theo contract (Owner)
**GET** `/api/bills/contract/{contractId}`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:** `200 OK` - Danh sách hóa đơn của contract

---

### 6. Lấy hóa đơn theo room code (Owner)
**GET** `/api/bills/room/{roomCode}`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:** `200 OK` - Danh sách hóa đơn của phòng

---

### 7. Cập nhật hóa đơn (Owner)
**PUT** `/api/bills/{billId}`

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "electricityCost": 250000,
  "waterCost": 60000,
  "serviceCost": 150000,
  "dueDate": "2026-03-10",
  "note": "Đã cập nhật chi phí điện nước"
}
```

**Response:** `200 OK` - Bill đã cập nhật với totalAmount mới

---

### 8. Xác nhận thanh toán (Owner & Tenant)
**POST** `/api/bills/{billId}/confirm-payment`

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "paymentMethod": "Chuyển khoản",
  "transactionCode": "TXN123456789",
  "note": "Đã thanh toán qua Vietcombank"
}
```

**Hoặc không cần truyền transactionCode (hệ thống tự sinh):**
```json
{
  "paymentMethod": "Chuyển khoản",
  "note": "Đã thanh toán qua Vietcombank"
}
```
Hệ thống sẽ tự động sinh mã giao dịch theo format: `BILL-{billId}-{yyyyMMddHHmmss}` (ví dụ: BILL-1-20260202-123456)

**Response:** `200 OK`
```json
{
  "billId": 1,
  "status": "PAID",
  "paymentDate": "2026-02-01T14:30:00",
  "paymentMethod": "Chuyển khoản",
  "transactionCode": "BILL-1-20260201-143000",
  ...
}
```
**Lưu ý:** Nếu không truyền `transactionCode`, hệ thống tự động sinh mã

---

### 9. Hủy hóa đơn (Owner)
**DELETE** `/api/bills/{billId}`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:** `204 No Content`

---

### 10. Tự động tạo hóa đơn hàng tháng (Manual trigger)
**POST** `/api/bills/generate-monthly`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:** `200 OK`
```json
"Monthly bills generated successfully"
```

---

## Trạng thái hóa đơn (BillStatus)
- `PENDING` - Chưa thanh toán
- `PAID` - Đã thanh toán
- `OVERDUE` - Quá hạn
- `CANCELLED` - Đã hủy

---

## Scheduler
Hệ thống tự động chạy vào **ngày 25 hàng tháng lúc 00:00** để tạo hóa đơn cho tất cả các contract đang ACTIVE.

### Cron Schedule:
```java
@Scheduled(cron = "0 0 0 25 * ?")
```

---

## Quy trình thanh toán
1. **Ngày 25 hàng tháng**: Hệ thống tự động tạo hóa đơn cho tất cả contract ACTIVE, tự động lấy giá từ Contract
2. **Tenant/Owner xem hóa đơn**: Qua API GET `/api/bills/tenant` hoặc `/api/bills/owner`
3. **Owner cập nhật chi phí điện nước**: Qua API PUT `/api/bills/{billId}` (nếu cần)
4. **Tenant thanh toán**: Chuyển khoản cho chủ nhà (ngoài hệ thống)
5. **Xác nhận thanh toán**: Tenant hoặc Owner gọi API POST `/api/bills/{billId}/confirm-payment`
6. **Hóa đơn được đánh dấu PAID**: Lưu lại thông tin thanh toán

---

## Flow tạo hóa đơn
### Tạo thủ công (Owner):
- Chỉ cần: `contractId` hoặc `roomCode` + `billingMonth` + `billingYear`
- Hệ thống tự động lấy: roomPrice (từ monthlyRent), serviceCost (từ serviceFee), roomCode, tenant, owner
- electricityCost và waterCost ban đầu = 0, Owner sẽ cập nhật sau

### Tạo tự động (Scheduler):
- Ngày 25 hàng tháng, hệ thống tự động tạo hóa đơn cho tất cả contract ACTIVE
- Tất cả dữ liệu tự động lấy từ Contract
- Hóa đơn có status = PENDING, chờ Owner cập nhật chi phí điện nước

---

## Notes
- Chỉ có thể cập nhật hóa đơn khi status là PENDING
- Không thể hủy hóa đơn đã thanh toán
- Mỗi contract chỉ có 1 hóa đơn cho mỗi tháng/năm
- Hạn thanh toán mặc định: ngày 5 của tháng tiếp theo
