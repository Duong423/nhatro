# API Lấy Tất Cả Bookings Cho Owner

## Endpoint
```
GET /api/bookings/owner/all
```

## Mô tả
API này cho phép **Owner** lấy danh sách tất cả bookings của tất cả các hostels mà họ sở hữu trong một lần gọi duy nhất.

## Authorization
Yêu cầu: **Owner role** và **Bearer Token**

## Request

### Headers
```
Authorization: Bearer {access_token}
Content-Type: application/json
```

### Parameters
Không có parameters (API tự động lấy tất cả bookings dựa trên owner đang đăng nhập)

## Response

### Success Response (200 OK)

```json
{
  "code": 200,
  "message": "Retrieved all bookings successfully",
  "result": [
    {
      "bookingId": 1,
      "customerId": 5,
      "customerName": "Nguyễn Văn A",
      "customerPhone": "0901234567",
      "customerEmail": "nguyenvana@gmail.com",
      "hostelId": 1,
      "hostelName": "Nhà trọ ABC",
      "hostelAddress": "123 Đường XYZ, Quận 1",
      "bookingDate": "2026-01-27T15:30:00",
      "checkInDate": "2026-02-15T14:00:00",
      "depositAmount": 900000,
      "status": "CONFIRMED",
      "notes": "Tôi sẽ đến lúc 2h chiều",
      "payment": {
        "paymentId": 1,
        "amount": 900000,
        "paymentMethod": "BANKING",
        "status": "COMPLETED",
        "transactionId": "TXN-1738047000000",
        "note": "Deposit payment for booking #1",
        "paidAt": "2026-01-27T15:30:00"
      },
      "createdAt": "2026-01-27T15:30:00"
    },
    {
      "bookingId": 2,
      "customerId": 8,
      "customerName": "Trần Thị B",
      "customerPhone": "0912345678",
      "customerEmail": "tranthib@gmail.com",
      "hostelId": 3,
      "hostelName": "Nhà trọ XYZ",
      "hostelAddress": "456 Đường ABC, Quận 2",
      "bookingDate": "2026-01-26T10:00:00",
      "checkInDate": "2026-03-01T10:00:00",
      "depositAmount": 1200000,
      "status": "CONFIRMED",
      "notes": "Cần hỗ trợ chuyển đồ",
      "payment": {
        "paymentId": 2,
        "amount": 1200000,
        "paymentMethod": "MOMO",
        "status": "COMPLETED",
        "transactionId": "TXN-1737960000000",
        "note": "Deposit payment for booking #2",
        "paidAt": "2026-01-26T10:00:00"
      },
      "createdAt": "2026-01-26T10:00:00"
    }
  ]
}
```

### Success với danh sách rỗng (200 OK)
Nếu owner chưa có booking nào:
```json
{
  "code": 200,
  "message": "Retrieved all bookings successfully",
  "result": []
}
```

### Error Response (401 Unauthorized)
Khi không có token hoặc token không hợp lệ:
```json
{
  "code": 401,
  "message": "Unauthorized",
  "result": null
}
```

### Error Response (403 Forbidden)
Khi user không phải là Owner:
```json
{
  "code": 403,
  "message": "Access denied. Owner role required",
  "result": null
}
```

### Error Response (400 Bad Request)
Lỗi khác:
```json
{
  "code": 400,
  "message": "Error retrieving bookings: {error_detail}",
  "result": null
}
```

## Response Fields

### Booking Object
| Field | Type | Description |
|-------|------|-------------|
| `bookingId` | Long | ID của booking |
| `customerId` | Long | ID của khách hàng đặt phòng |
| `customerName` | String | Tên khách hàng |
| `customerPhone` | String | Số điện thoại khách hàng |
| `customerEmail` | String | Email khách hàng |
| `hostelId` | Long | ID của hostel được đặt |
| `hostelName` | String | Tên hostel |
| `hostelAddress` | String | Địa chỉ hostel |
| `bookingDate` | DateTime | Ngày tạo booking |
| `checkInDate` | DateTime | Ngày dự kiến check-in |
| `depositAmount` | BigDecimal | Số tiền cọc (VNĐ) |
| `status` | String | Trạng thái booking |
| `notes` | String | Ghi chú từ khách hàng |
| `payment` | Object | Thông tin thanh toán |
| `createdAt` | DateTime | Ngày tạo |

### Payment Object
| Field | Type | Description |
|-------|------|-------------|
| `paymentId` | Long | ID của payment |
| `amount` | BigDecimal | Số tiền đã thanh toán (VNĐ) |
| `paymentMethod` | String | Phương thức thanh toán |
| `status` | String | Trạng thái thanh toán |
| `transactionId` | String | Mã giao dịch |
| `note` | String | Ghi chú |
| `paidAt` | DateTime | Thời gian thanh toán |

## Booking Status Values
- `PENDING` - Đang chờ thanh toán
- `CONFIRMED` - Đã xác nhận (đã thanh toán tiền cọc)
- `CANCELLED` - Đã hủy
- `COMPLETED` - Đã hoàn thành

## Payment Status Values
- `PENDING` - Chờ thanh toán
- `COMPLETED` - Đã thanh toán thành công
- `FAILED` - Thanh toán thất bại
- `REFUNDED` - Đã hoàn tiền

## Payment Methods
- `CASH` - Tiền mặt
- `BANKING` - Chuyển khoản ngân hàng
- `MOMO` - Ví MoMo
- `VNPAY` - Ví VNPay

## Frontend Implementation Example

### JavaScript (Fetch API)
```javascript
async function getAllBookingsForOwner() {
  const accessToken = localStorage.getItem('accessToken');
  
  try {
    const response = await fetch('http://localhost:8080/api/bookings/owner/all', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    });
    
    const data = await response.json();
    
    if (data.code === 200) {
      console.log('Bookings:', data.result);
      return data.result;
    } else {
      console.error('Error:', data.message);
      throw new Error(data.message);
    }
  } catch (error) {
    console.error('Failed to fetch bookings:', error);
    throw error;
  }
}
```

### Axios
```javascript
import axios from 'axios';

const getAllBookingsForOwner = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/bookings/owner/all', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
      }
    });
    
    if (response.data.code === 200) {
      return response.data.result;
    }
  } catch (error) {
    console.error('Error fetching bookings:', error);
    throw error;
  }
};
```

### React Example with Hooks
```jsx
import { useState, useEffect } from 'react';
import axios from 'axios';

function OwnerBookingsList() {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchBookings = async () => {
      try {
        const token = localStorage.getItem('accessToken');
        const response = await axios.get(
          'http://localhost:8080/api/bookings/owner/all',
          {
            headers: { Authorization: `Bearer ${token}` }
          }
        );
        
        if (response.data.code === 200) {
          setBookings(response.data.result);
        }
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchBookings();
  }, []);

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div className="bookings-list">
      <h2>Tất cả Bookings ({bookings.length})</h2>
      {bookings.length === 0 ? (
        <p>Chưa có booking nào</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Hostel</th>
              <th>Khách hàng</th>
              <th>Ngày check-in</th>
              <th>Tiền cọc</th>
              <th>Trạng thái</th>
            </tr>
          </thead>
          <tbody>
            {bookings.map(booking => (
              <tr key={booking.bookingId}>
                <td>{booking.bookingId}</td>
                <td>{booking.hostelName}</td>
                <td>
                  {booking.customerName}<br/>
                  {booking.customerPhone}
                </td>
                <td>{new Date(booking.checkInDate).toLocaleDateString('vi-VN')}</td>
                <td>{booking.depositAmount.toLocaleString('vi-VN')} đ</td>
                <td>
                  <span className={`status-${booking.status.toLowerCase()}`}>
                    {booking.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
```

## Curl Command (Testing)
```bash
curl -X GET http://localhost:8080/api/bookings/owner/all \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

## Use Cases

1. **Dashboard Overview**: Hiển thị tổng quan tất cả bookings cho owner
2. **Revenue Tracking**: Tính tổng doanh thu từ tiền cọc
3. **Booking Management**: Quản lý tất cả bookings từ một nơi
4. **Statistics**: Thống kê số lượng booking theo trạng thái
5. **Customer Management**: Xem danh sách tất cả khách hàng đã đặt phòng

## Notes

- API này chỉ dành cho **Owner role**
- Tự động lấy tất cả bookings của tất cả hostels mà owner sở hữu
- Trả về array rỗng nếu owner chưa có booking nào
- Mỗi booking bao gồm đầy đủ thông tin payment
- Dữ liệu được sắp xếp theo thứ tự mặc định từ database
- **Lưu ý**: 1 hostel chỉ có tối đa 1 booking (OneToOne relationship)

## Error Handling Best Practices

```javascript
async function fetchBookingsWithErrorHandling() {
  try {
    const response = await fetch('http://localhost:8080/api/bookings/owner/all', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('accessToken')}`,
        'Content-Type': 'application/json'
      }
    });
    
    const data = await response.json();
    
    switch(data.code) {
      case 200:
        return data.result;
      case 401:
        // Redirect to login
        window.location.href = '/login';
        break;
      case 403:
        alert('Bạn không có quyền truy cập trang này');
        break;
      case 400:
        alert(`Lỗi: ${data.message}`);
        break;
      default:
        alert('Có lỗi xảy ra, vui lòng thử lại');
    }
  } catch (error) {
    console.error('Network error:', error);
    alert('Không thể kết nối đến server');
  }
}
```
