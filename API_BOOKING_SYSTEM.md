# API Đặt Phòng (Booking System)

## Tổng quan Flow Đặt Phòng

1. **Owner tạo/cập nhật hostel** với thông tin tiền cọc (`depositAmount`)
2. **Khách xem thông tin hostel** bao gồm tiền cọc và trạng thái phòng
3. **Khách tạo booking** và thanh toán tiền cọc
4. **Hệ thống tự động**:
   - Tạo booking với status PENDING
   - Tạo payment với depositAmount từ hostel
   - Cập nhật booking status thành CONFIRMED
   - Cập nhật hostel status thành FULL

---

## 1. API Tạo/Cập nhật Hostel (Owner)

### 1.1. Tạo Hostel với Tiền Cọc

**Endpoint**: `POST /api/hostels/create-with-images`

**Headers**:
```
Authorization: Bearer {access_token}
Content-Type: multipart/form-data
```

**Form Data**:
```
title: Nhà trọ ABC
address: 123 Đường XYZ, Quận 1
price: 3000000
depositAmount: 900000
area: 25
description: Phòng đẹp, thoáng mát
amenities: wifi,điều hòa,giường,tủ lạnh
imageFiles: [file1, file2, ...]
```

**Response**:
```json
{
  "code": 201,
  "message": "Hostel created successfully",
  "result": {
    "hostelId": 1,
    "name": "Nhà trọ ABC",
    "address": "123 Đường XYZ, Quận 1",
    "price": 3000000,
    "depositAmount": 900000,
    "status": "AVAILABLE",
    "imageUrls": ["url1", "url2"],
    ...
  }
}
```

### 1.2. Cập nhật Hostel (bao gồm Tiền Cọc)

**Endpoint**: `PUT /api/hostels/{hostelId}`

**Headers**:
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Body**:
```json
{
  "name": "Nhà trọ ABC Updated",
  "price": 3500000,
  "depositAmount": 1050000,
  "description": "Cập nhật mô tả"
}
```

---

## 2. API Xem Thông Tin Hostel (Khách)

### 2.1. Xem Danh Sách Hostel

**Endpoint**: `GET /api/hostels/tenant/detailsHostel`

**Response**:
```json
{
  "code": 200,
  "message": "Lấy chi tiết danh sách tất cả nhà trọ thành công",
  "result": [
    {
      "hostelId": 1,
      "name": "Nhà trọ ABC",
      "price": 3000000,
      "depositAmount": 900000,
      "status": "AVAILABLE",
      "address": "123 Đường XYZ",
      ...
    }
  ]
}
```

### 2.2. Xem Chi Tiết Hostel

**Endpoint**: `GET /api/hostels/tenant/detailsHostel/{hostelId}`

**Response**:
```json
{
  "code": 200,
  "message": "Success",
  "result": {
    "hostelId": 1,
    "name": "Nhà trọ ABC",
    "price": 3000000,
    "depositAmount": 900000,
    "status": "AVAILABLE",
    "imageUrls": ["url1", "url2"],
    ...
  }
}
```

---

## 3. API Đặt Phòng và Thanh Toán (Khách)

### 3.1. Tạo Booking và Thanh Toán Tiền Cọc

**Endpoint**: `POST /api/bookings/create`

**Headers**:
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Body**:
```json
{
  "hostelId": 1,
  "checkInDate": "2026-02-01T14:00:00",
  "customerName": "Nguyễn Văn A",
  "customerPhone": "0901234567",
  "customerEmail": "nguyenvana@email.com",
  "notes": "Tôi sẽ đến lúc 2h chiều",
  "paymentMethod": "BANKING"
}
```

**Response - Success**:
```json
{
  "code": 201,
  "message": "Booking created successfully and payment completed",
  "result": {
    "bookingId": 1,
    "customerId": 5,
    "customerName": "Nguyễn Văn A",
    "customerPhone": "0901234567",
    "hostelId": 1,
    "hostelName": "Nhà trọ ABC",
    "hostelAddress": "123 Đường XYZ",
    "bookingDate": "2026-01-27T10:30:00",
    "checkInDate": "2026-02-01T14:00:00",
    "depositAmount": 900000,
    "status": "CONFIRMED",
    "notes": "Tôi sẽ đến lúc 2h chiều",
    "payment": {
      "paymentId": 1,
      "amount": 900000,
      "paymentMethod": "BANKING",
      "status": "COMPLETED",
      "transactionId": "TXN-1738046400000",
      "note": "Deposit payment for booking #1",
      "paidAt": "2026-01-27T10:30:00"
    },
    "createdAt": "2026-01-27T10:30:00"
  }
}
```

**Response - Error**:
```json
{
  "code": 400,
  "message": "Error creating booking: Hostel is already full",
  "result": null
}
```

---

## 4. API Quản Lý Booking

### 4.1. Xem Danh Sách Booking Của Tôi

**Endpoint**: `GET /api/bookings/my-bookings`

**Headers**:
```
Authorization: Bearer {access_token}
```

**Response**:
```json
{
  "code": 200,
  "message": "Retrieved bookings successfully",
  "result": [
    {
      "bookingId": 1,
      "hostelName": "Nhà trọ ABC",
      "status": "CONFIRMED",
      "depositAmount": 900000,
      "checkInDate": "2026-02-01T14:00:00",
      "payment": { ... }
    }
  ]
}
```

### 4.2. Xem Chi Tiết Booking

**Endpoint**: `GET /api/bookings/{bookingId}`

**Headers**:
```
Authorization: Bearer {access_token}
```

### 4.3. Hủy Booking

**Endpoint**: `PUT /api/bookings/{bookingId}/cancel`

**Headers**:
```
Authorization: Bearer {access_token}
```

**Response**:
```json
{
  "code": 200,
  "message": "Booking cancelled successfully",
  "result": {
    "bookingId": 1,
    "status": "CANCELLED",
    ...
  }
}
```

**Lưu ý**: 
- Khi hủy booking, hostel status sẽ tự động chuyển về `AVAILABLE`
- Chỉ có thể hủy booking với status `PENDING` hoặc `CONFIRMED`

### 4.4. Owner Xem Booking Theo Hostel

**Endpoint**: `GET /api/bookings/hostel/{hostelId}`

**Headers**:
```
Authorization: Bearer {access_token}
```

**Response**:
```json
{
  "code": 200,
  "message": "Retrieved bookings for hostel successfully",
  "result": [
    {
      "bookingId": 1,
      "customerName": "Nguyễn Văn A",
      "customerPhone": "0901234567",
      "status": "CONFIRMED",
      "depositAmount": 900000,
      ...
    }
  ]
}
```

---

## Enum Values

### HostelStatus
- `AVAILABLE` - Còn phòng trống
- `FULL` - Đã đủ người thuê
- `CLOSED` - Tạm đóng cửa
- `UNDER_RENOVATION` - Đang sửa chữa

### BookingStatus
- `PENDING` - Đang chờ thanh toán
- `CONFIRMED` - Đã xác nhận (đã thanh toán)
- `CANCELLED` - Đã hủy
- `COMPLETED` - Đã hoàn thành

### PaymentStatus
- `PENDING` - Chờ thanh toán
- `COMPLETED` - Đã thanh toán
- `FAILED` - Thanh toán thất bại
- `REFUNDED` - Đã hoàn tiền

### Payment Methods
- `CASH` - Tiền mặt
- `BANKING` - Chuyển khoản ngân hàng
- `MOMO` - Ví MoMo
- `VNPAY` - Ví VNPay

---

## Business Logic

1. **Khi tạo booking**:
   - Kiểm tra hostel status phải là `AVAILABLE`
   - Lấy `depositAmount` từ hostel
   - Tạo booking với status `PENDING`
   - Tạo payment với status `COMPLETED` (giả sử thanh toán thành công)
   - Cập nhật booking status thành `CONFIRMED`
   - Cập nhật hostel status thành `FULL`

2. **Khi hủy booking**:
   - Chỉ có thể hủy booking với status `PENDING` hoặc `CONFIRMED`
   - Cập nhật booking status thành `CANCELLED`
   - Cập nhật hostel status về `AVAILABLE`

3. **Quyền truy cập**:
   - Customer chỉ xem được booking của mình
   - Owner có thể xem tất cả booking của hostel mình sở hữu
   - Chỉ customer tạo booking mới có thể hủy booking đó
