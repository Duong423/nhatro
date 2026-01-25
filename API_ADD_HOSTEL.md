# API: Thêm mới Hostel (Tạo nhà trọ mới)

## Endpoint
```
POST /api/hostels/create-with-images
```

## Mô tả
Tạo mới một hostel (nhà trọ) kèm upload nhiều ảnh và danh sách dịch vụ. Nhận dữ liệu dạng `multipart/form-data`.

## Yêu cầu xác thực
- Header: `Authorization: Bearer <token>` (role OWNER)

## Request Body (form-data)
| Tên field      | Kiểu dữ liệu      | Bắt buộc | Mô tả |
|----------------|------------------|----------|-------|
| title          | string           | Có       | Tiêu đề nhà trọ |
| address        | string           | Có       | Địa chỉ |
| district       | string           | Có       | Quận/huyện |
| city           | string           | Có       | Thành phố |
| price          | number           | Có       | Giá phòng (VNĐ) |
| area           | number           | Không    | Diện tích (m2) |
| description    | string           | Có       | Mô tả chi tiết |
| amenities      | string           | Không    | Tiện ích bổ sung (chuỗi, ví dụ: "Wifi, Máy lạnh") |
| roomCount      | number           | Có       | Số lượng phòng |
| maxOccupancy   | number           | Có       | Số người tối đa/phòng |
| roomType       | string           | Có       | Loại phòng (ví dụ: "DORM", "PRIVATE") |
| imageFiles     | file (nhiều file)| Có       | 1 hoặc nhiều ảnh (chọn nhiều file) |
| servicesJson   | string (JSON)    | Không    | Danh sách dịch vụ dạng JSON, ví dụ: `[{"name": "Wifi", "price": 50000}]` |

### Ví dụ `servicesJson`
```
[
  {"name": "Wifi", "price": 50000},
  {"name": "Gửi xe", "price": 100000}
]
```

## Ví dụ request với Postman
- Chọn Body: form-data
- Thêm các trường như bảng trên
- Trường `imageFiles`: chọn loại File, có thể chọn nhiều ảnh
- Trường `servicesJson`: copy chuỗi JSON như ví dụ

## Response
- Thành công: HTTP 201, trả về object hostel vừa tạo
- Thất bại: HTTP 400, trả về message lỗi

### Response mẫu
```json
{
  "code": 201,
  "message": "Hostel created successfully",
  "result": {
    "hostelId": 123,
    "name": "Nhà trọ test mới",
    "address": "123 Đường Test",
    "district": "Quận 1",
    "city": "TPHCM",
    "price": 3000000,
    "area": 25,
    "description": "Mô tả chi tiết",
    "amenities": "Wifi, Máy lạnh",
    "roomCount": 10,
    "maxOccupancy": 3,
    "roomType": "DORM",
    "images": [
      "https://res.cloudinary.com/.../image1.jpg",
      "https://res.cloudinary.com/.../image2.jpg"
    ],
    "services": [
      {"name": "Wifi", "price": 50000},
      {"name": "Gửi xe", "price": 100000}
    ]
  }
}
```

## Lưu ý
- Nếu gửi nhiều ảnh, backend sẽ upload lên Cloudinary và trả về link ảnh.
- Nếu không truyền `servicesJson`, hostel vẫn được tạo bình thường.
- Nếu thiếu trường bắt buộc, API trả về lỗi 400.
