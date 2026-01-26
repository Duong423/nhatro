# API Thêm Mới Nhà Trọ

## Endpoint
```
POST /api/hostels/create-with-images
```

## Authentication
- **Required**: Yes
- **Role**: OWNER
- **Header**: `Authorization: Bearer <jwt_token>`

## Content-Type
```
multipart/form-data
```

## Request Parameters

### Required Fields

| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `title` | String | Tên nhà trọ | Bắt buộc, không được để trống |
| `address` | String | Địa chỉ nhà trọ | Bắt buộc, không được để trống |
| `price` | Number | Giá thuê (VNĐ) | Bắt buộc, phải là số dương |
| `description` | String | Mô tả chi tiết về nhà trọ | Bắt buộc, không được để trống |

### Optional Fields

| Field | Type | Description | Default |
|-------|------|-------------|---------|
| `area` | Number | Diện tích (m²) | null |
| `amenities` | String | Tiện ích (wifi, điều hòa, v.v.) | null |
| `imageFiles` | File[] | Danh sách file ảnh (upload) | [] |

## Request Example

### Using JavaScript FormData

```javascript
const formData = new FormData();

// Required fields
formData.append('title', 'Nhà trọ cao cấp quận 1');
formData.append('address', '123 Nguyễn Huệ, Quận 1, TP.HCM');
formData.append('price', '3500000');
formData.append('description', 'Phòng trọ rộng rãi, thoáng mát, đầy đủ tiện nghi');

// Optional fields
formData.append('area', '25');
formData.append('amenities', 'Wifi, Điều hòa, Máy giặt, Tủ lạnh');

// Upload multiple images
const imageFiles = document.querySelector('#imageInput').files;
for (let i = 0; i < imageFiles.length; i++) {
  formData.append('imageFiles', imageFiles[i]);
}

// API call
fetch('http://localhost:8080/api/hostels/create-with-images', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + token
  },
  body: formData
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```

### Using Axios

```javascript
import axios from 'axios';

const createHostel = async (hostelData, imageFiles) => {
  const formData = new FormData();
  
  // Add text fields
  formData.append('title', hostelData.title);
  formData.append('address', hostelData.address);
  formData.append('price', hostelData.price);
  formData.append('description', hostelData.description);
  formData.append('area', hostelData.area);
  formData.append('amenities', hostelData.amenities);
  
  // Add image files
  imageFiles.forEach(file => {
    formData.append('imageFiles', file);
  });
  
  try {
    const response = await axios.post(
      'http://localhost:8080/api/hostels/create-with-images',
      formData,
      {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'multipart/form-data'
        }
      }
    );
    return response.data;
  } catch (error) {
    console.error('Error creating hostel:', error);
    throw error;
  }
};
```

## Response

### Success Response (201 Created)

```json
{
  "code": 201,
  "message": "Hostel created successfully",
  "result": {
    "hostelId": 1,
    "ownerId": 5,
    "ownerName": "Nguyễn Văn A",
    "name": "Nhà trọ cao cấp quận 1",
    "address": "123 Nguyễn Huệ, Quận 1, TP.HCM",
    "price": 3500000.0,
    "area": 25.0,
    "contactName": "Nguyễn Văn A",
    "contactPhone": "0901234567",
    "contactEmail": "owner@example.com",
    "imageUrls": [
      "https://res.cloudinary.com/dp0yccmop/image/upload/v1234567890/nhatro/abc123.jpg",
      "https://res.cloudinary.com/dp0yccmop/image/upload/v1234567890/nhatro/def456.jpg"
    ],
    "description": "Phòng trọ rộng rãi, thoáng mát, đầy đủ tiện nghi",
    "amenities": "Wifi, Điều hòa, Máy giặt, Tủ lạnh",
    "createdAt": "2026-01-27T00:15:30"
  }
}
```

### Error Responses

#### 400 Bad Request - Missing Required Fields
```json
{
  "code": 400,
  "message": "Missing required fields (title, address, price)",
  "result": null
}
```

#### 400 Bad Request - Invalid Content Type
```json
{
  "code": 400,
  "message": "Request must be multipart/form-data",
  "result": null
}
```

#### 400 Bad Request - General Error
```json
{
  "code": 400,
  "message": "Error creating hostel: Invalid price format",
  "result": null
}
```

#### 401 Unauthorized
```json
{
  "code": 401,
  "message": "Unauthorized - Token missing or invalid",
  "result": null
}
```

#### 403 Forbidden
```json
{
  "code": 403,
  "message": "Access denied - OWNER role required",
  "result": null
}
```

## Notes

### Image Upload
- Hệ thống tự động upload ảnh lên **Cloudinary**
- Ảnh được lưu trong folder `nhatro/`
- Link ảnh từ Cloudinary sẽ được lưu vào database
- Hỗ trợ nhiều định dạng: JPG, PNG, JPEG, WebP
- Giới hạn kích thước: 1000MB per file
- Giới hạn tổng request: 1000MB

### Quy trình xử lý
1. Client gửi form-data với thông tin và file ảnh
2. Server validate dữ liệu đầu vào
3. Upload từng ảnh lên Cloudinary
4. Lấy URL của ảnh từ Cloudinary
5. Lưu thông tin hostel và URL ảnh vào database
6. Trả về thông tin hostel đã tạo kèm URL ảnh

### Best Practices
- Nén ảnh trước khi upload để tối ưu tốc độ
- Hiển thị progress bar khi upload
- Validate file type ở client trước khi gửi
- Xử lý lỗi và hiển thị message phù hợp cho user
- Sử dụng lazy loading cho ảnh trong danh sách

## HTML Form Example

```html
<form id="createHostelForm" enctype="multipart/form-data">
  <div>
    <label>Tên nhà trọ *</label>
    <input type="text" name="title" required>
  </div>
  
  <div>
    <label>Địa chỉ *</label>
    <input type="text" name="address" required>
  </div>
  
  <div>
    <label>Giá thuê (VNĐ) *</label>
    <input type="number" name="price" min="0" required>
  </div>
  
  <div>
    <label>Diện tích (m²)</label>
    <input type="number" name="area" step="0.1" min="0">
  </div>
  
  <div>
    <label>Mô tả *</label>
    <textarea name="description" required></textarea>
  </div>
  
  <div>
    <label>Tiện ích</label>
    <input type="text" name="amenities" placeholder="Wifi, Điều hòa, Máy giặt...">
  </div>
  
  <div>
    <label>Hình ảnh</label>
    <input type="file" name="imageFiles" multiple accept="image/*">
  </div>
  
  <button type="submit">Tạo nhà trọ</button>
</form>

<script>
document.getElementById('createHostelForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  
  const formData = new FormData(e.target);
  const token = localStorage.getItem('token');
  
  try {
    const response = await fetch('http://localhost:8080/api/hostels/create-with-images', {
      method: 'POST',
      headers: {
        'Authorization': 'Bearer ' + token
      },
      body: formData
    });
    
    const data = await response.json();
    
    if (data.code === 201) {
      alert('Tạo nhà trọ thành công!');
      window.location.href = '/my-hostels';
    } else {
      alert('Lỗi: ' + data.message);
    }
  } catch (error) {
    alert('Lỗi kết nối: ' + error.message);
  }
});
</script>
```

## Testing with Postman

1. Set request type to **POST**
2. URL: `http://localhost:8080/api/hostels/create-with-images`
3. Authorization tab: Select **Bearer Token**, paste JWT token
4. Body tab: Select **form-data**
5. Add key-value pairs:
   - `title`: Text - "Nhà trọ test"
   - `address`: Text - "123 Test Street"
   - `price`: Text - "3000000"
   - `description`: Text - "Mô tả test"
   - `area`: Text - "20"
   - `amenities`: Text - "Wifi, AC"
   - `imageFiles`: File - Select image files (can add multiple)
6. Click **Send**
