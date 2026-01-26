# API Cập Nhật Nhà Trọ

## Overview
Tài liệu này mô tả 2 API liên quan đến cập nhật thông tin nhà trọ:
1. **API cập nhật ảnh** - Upload/xóa ảnh hostel
2. **API cập nhật thông tin** - Cập nhật thông tin chi tiết hostel

---

## 1. API Cập Nhật Ảnh Hostel

### Endpoint
```
PUT /api/hostels/{hostelId}/images
```

### Authentication
- **Required**: Yes
- **Role**: OWNER
- **Header**: `Authorization: Bearer <jwt_token>`

### Content-Type
```
multipart/form-data
```

### Path Parameters
| Parameter | Type | Description |
|-----------|------|-------------|
| `hostelId` | Long | ID của hostel cần cập nhật ảnh |

### Request Parameters

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `imageFiles` | File[] | No | Danh sách file ảnh mới cần upload |
| `keepImages` | String[] | No | Danh sách URL ảnh cũ muốn giữ lại |

### Use Cases

#### Case 1: Thêm ảnh mới (giữ lại ảnh cũ)
```javascript
const formData = new FormData();

// Giữ lại các ảnh cũ
formData.append('keepImages', 'https://res.cloudinary.com/.../old1.jpg');
formData.append('keepImages', 'https://res.cloudinary.com/.../old2.jpg');

// Thêm ảnh mới
const newFiles = document.querySelector('#newImageInput').files;
for (let file of newFiles) {
  formData.append('imageFiles', file);
}

const response = await fetch(`http://localhost:8080/api/hostels/${hostelId}/images`, {
  method: 'PUT',
  headers: {
    'Authorization': `Bearer ${token}`
  },
  body: formData
});
```

#### Case 2: Xóa một số ảnh cũ, thêm ảnh mới
```javascript
const formData = new FormData();

// Chỉ giữ lại một số ảnh cũ (ảnh không có trong keepImages sẽ bị xóa)
formData.append('keepImages', 'https://res.cloudinary.com/.../keep1.jpg');

// Thêm ảnh mới
for (let file of newFiles) {
  formData.append('imageFiles', file);
}
```

#### Case 3: Xóa toàn bộ ảnh cũ, upload ảnh mới
```javascript
const formData = new FormData();

// Không truyền keepImages -> xóa hết ảnh cũ
// Chỉ upload ảnh mới
for (let file of newFiles) {
  formData.append('imageFiles', file);
}
```

### Response

#### Success Response (200 OK)
```json
{
  "code": 200,
  "message": "Images updated successfully",
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
      "https://res.cloudinary.com/.../old1.jpg",
      "https://res.cloudinary.com/.../new1.jpg",
      "https://res.cloudinary.com/.../new2.jpg"
    ],
    "description": "Phòng trọ rộng rãi...",
    "amenities": "Wifi, Điều hòa...",
    "createdAt": "2026-01-27T00:15:30"
  }
}
```

#### Error Responses
```json
{
  "code": 400,
  "message": "Failed to update images: Hostel not found",
  "result": null
}
```

---

## 2. API Cập Nhật Thông Tin Hostel

### Endpoint
```
PUT /api/hostels/{hostelId}
```

### Authentication
- **Required**: Yes
- **Role**: OWNER
- **Header**: `Authorization: Bearer <jwt_token>`

### Content-Type
```
application/json
```

### Path Parameters
| Parameter | Type | Description |
|-----------|------|-------------|
| `hostelId` | Long | ID của hostel cần cập nhật |

### Request Body

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | String | Yes | Tên nhà trọ |
| `address` | String | Yes | Địa chỉ |
| `description` | String | Yes | Mô tả chi tiết |
| `price` | Number | Yes | Giá thuê (VNĐ) |
| `area` | Number | Yes | Diện tích (m²) |
| `amenities` | String | No | Tiện ích |
| `contactName` | String | No | Tên người liên hệ |
| `contactPhone` | String | No | Số điện thoại liên hệ |
| `contactEmail` | String | No | Email liên hệ |
| `images` | String[] | No | Danh sách URL ảnh |

### Request Example

```javascript
const updateData = {
  name: "Nhà trọ cao cấp quận 1 - Đã cập nhật",
  address: "123 Nguyễn Huệ, Quận 1, TP.HCM",
  description: "Phòng trọ rộng rãi, thoáng mát, view đẹp",
  price: 4000000,
  area: 30,
  amenities: "Wifi, Điều hòa, Máy giặt, Tủ lạnh, Ban công",
  contactName: "Nguyễn Văn A",
  contactPhone: "0901234567",
  contactEmail: "contact@example.com",
  images: [
    "https://res.cloudinary.com/.../image1.jpg",
    "https://res.cloudinary.com/.../image2.jpg",
    "https://res.cloudinary.com/.../image3.jpg"
  ]
};

const response = await fetch(`http://localhost:8080/api/hostels/${hostelId}`, {
  method: 'PUT',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(updateData)
});

const data = await response.json();
console.log(data);
```

### Response

#### Success Response (200 OK)
```json
{
  "code": 200,
  "message": "Cập nhật hostel thành công",
  "result": {
    "hostelId": 1,
    "name": "Nhà trọ cao cấp quận 1 - Đã cập nhật",
    "address": "123 Nguyễn Huệ, Quận 1, TP.HCM",
    "description": "Phòng trọ rộng rãi, thoáng mát, view đẹp",
    "price": 4000000.0,
    "contactPhone": "0901234567",
    "contactEmail": "contact@example.com",
    "contactName": "Nguyễn Văn A",
    "area": 30.0,
    "amenities": "Wifi, Điều hòa, Máy giặt, Tủ lạnh, Ban công",
    "imageUrls": [
      "https://res.cloudinary.com/.../image1.jpg",
      "https://res.cloudinary.com/.../image2.jpg",
      "https://res.cloudinary.com/.../image3.jpg"
    ]
  }
}
```

#### Error Responses
```json
{
  "code": 400,
  "message": "Cập nhật hostel thất bại: Hostel not found",
  "result": null
}
```

---

## Workflow Tích Hợp Frontend

### Cách 1: Cập nhật ảnh trước, sau đó cập nhật thông tin

```javascript
// Step 1: Upload/Update images first
async function updateHostelImages(hostelId, newFiles, keepImageUrls) {
  const formData = new FormData();
  
  // Add images to keep
  if (keepImageUrls && keepImageUrls.length > 0) {
    keepImageUrls.forEach(url => formData.append('keepImages', url));
  }
  
  // Add new image files
  if (newFiles && newFiles.length > 0) {
    for (let file of newFiles) {
      formData.append('imageFiles', file);
    }
  }
  
  const response = await fetch(`http://localhost:8080/api/hostels/${hostelId}/images`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    },
    body: formData
  });
  
  return await response.json();
}

// Step 2: Update hostel info with new image URLs
async function updateHostelInfo(hostelId, hostelData, imageUrls) {
  const updateData = {
    ...hostelData,
    images: imageUrls
  };
  
  const response = await fetch(`http://localhost:8080/api/hostels/${hostelId}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(updateData)
  });
  
  return await response.json();
}

// Main update function
async function updateHostel(hostelId, hostelData, newImageFiles, keepImageUrls) {
  try {
    // Step 1: Update images
    const imageResponse = await updateHostelImages(hostelId, newImageFiles, keepImageUrls);
    
    if (imageResponse.code !== 200) {
      throw new Error(imageResponse.message);
    }
    
    // Step 2: Update info with new image URLs
    const finalResponse = await updateHostelInfo(
      hostelId, 
      hostelData, 
      imageResponse.result.imageUrls
    );
    
    if (finalResponse.code === 200) {
      alert('Cập nhật nhà trọ thành công!');
      return finalResponse.result;
    } else {
      throw new Error(finalResponse.message);
    }
  } catch (error) {
    alert('Lỗi: ' + error.message);
    throw error;
  }
}

// Usage
const hostelData = {
  name: "Nhà trọ mới",
  address: "123 ABC",
  description: "Mô tả...",
  price: 3000000,
  area: 25,
  amenities: "Wifi, AC",
  contactName: "John Doe",
  contactPhone: "0901234567",
  contactEmail: "john@example.com"
};

const newFiles = document.querySelector('#imageInput').files;
const keepUrls = ['https://res.cloudinary.com/.../old1.jpg'];

await updateHostel(1, hostelData, newFiles, keepUrls);
```

### Cách 2: Chỉ cập nhật thông tin (không thay đổi ảnh)

```javascript
async function updateHostelInfoOnly(hostelId, hostelData) {
  // Giữ nguyên images hiện tại
  const response = await fetch(`http://localhost:8080/api/hostels/${hostelId}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(hostelData)
  });
  
  return await response.json();
}
```

### Cách 3: Chỉ cập nhật ảnh (không thay đổi thông tin)

```javascript
async function updateImagesOnly(hostelId, newFiles, keepUrls) {
  const formData = new FormData();
  
  if (keepUrls) {
    keepUrls.forEach(url => formData.append('keepImages', url));
  }
  
  if (newFiles) {
    for (let file of newFiles) {
      formData.append('imageFiles', file);
    }
  }
  
  const response = await fetch(`http://localhost:8080/api/hostels/${hostelId}/images`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    },
    body: formData
  });
  
  return await response.json();
}
```

---

## React Example - Complete Update Form

```jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';

const UpdateHostelForm = ({ hostelId }) => {
  const [formData, setFormData] = useState({
    name: '',
    address: '',
    description: '',
    price: '',
    area: '',
    amenities: '',
    contactName: '',
    contactPhone: '',
    contactEmail: ''
  });
  
  const [currentImages, setCurrentImages] = useState([]);
  const [newImages, setNewImages] = useState([]);
  const [imagesToKeep, setImagesToKeep] = useState([]);
  const [loading, setLoading] = useState(false);
  
  // Load current hostel data
  useEffect(() => {
    fetchHostelData();
  }, [hostelId]);
  
  const fetchHostelData = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/api/hostels/tenant/detailsHostel/${hostelId}`,
        {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        }
      );
      
      const hostel = response.data.result;
      setFormData({
        name: hostel.name,
        address: hostel.address,
        description: hostel.description,
        price: hostel.price,
        area: hostel.area,
        amenities: hostel.amenities || '',
        contactName: hostel.contactName || '',
        contactPhone: hostel.contactPhone || '',
        contactEmail: hostel.contactEmail || ''
      });
      
      setCurrentImages(hostel.imageUrls || []);
      setImagesToKeep(hostel.imageUrls || []);
    } catch (error) {
      console.error('Error fetching hostel:', error);
    }
  };
  
  const handleInputChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };
  
  const handleImageChange = (e) => {
    setNewImages(Array.from(e.target.files));
  };
  
  const handleRemoveImage = (imageUrl) => {
    setImagesToKeep(imagesToKeep.filter(url => url !== imageUrl));
  };
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      const token = localStorage.getItem('token');
      
      // Step 1: Update images if there are changes
      let finalImageUrls = imagesToKeep;
      
      if (newImages.length > 0 || imagesToKeep.length !== currentImages.length) {
        const imageFormData = new FormData();
        
        imagesToKeep.forEach(url => imageFormData.append('keepImages', url));
        newImages.forEach(file => imageFormData.append('imageFiles', file));
        
        const imageResponse = await axios.put(
          `http://localhost:8080/api/hostels/${hostelId}/images`,
          imageFormData,
          {
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'multipart/form-data'
            }
          }
        );
        
        finalImageUrls = imageResponse.data.result.imageUrls;
      }
      
      // Step 2: Update hostel info
      const updateData = {
        ...formData,
        price: parseFloat(formData.price),
        area: parseFloat(formData.area),
        images: finalImageUrls
      };
      
      const response = await axios.put(
        `http://localhost:8080/api/hostels/${hostelId}`,
        updateData,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );
      
      alert('Cập nhật thành công!');
      fetchHostelData(); // Refresh data
      
    } catch (error) {
      alert('Lỗi: ' + error.message);
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <form onSubmit={handleSubmit}>
      <h2>Cập nhật thông tin nhà trọ</h2>
      
      <div>
        <label>Tên nhà trọ *</label>
        <input
          type="text"
          name="name"
          value={formData.name}
          onChange={handleInputChange}
          required
        />
      </div>
      
      <div>
        <label>Địa chỉ *</label>
        <input
          type="text"
          name="address"
          value={formData.address}
          onChange={handleInputChange}
          required
        />
      </div>
      
      <div>
        <label>Giá thuê (VNĐ) *</label>
        <input
          type="number"
          name="price"
          value={formData.price}
          onChange={handleInputChange}
          required
        />
      </div>
      
      <div>
        <label>Diện tích (m²) *</label>
        <input
          type="number"
          name="area"
          value={formData.area}
          onChange={handleInputChange}
          required
        />
      </div>
      
      <div>
        <label>Mô tả *</label>
        <textarea
          name="description"
          value={formData.description}
          onChange={handleInputChange}
          required
        />
      </div>
      
      <div>
        <label>Tiện ích</label>
        <input
          type="text"
          name="amenities"
          value={formData.amenities}
          onChange={handleInputChange}
          placeholder="Wifi, Điều hòa, Máy giặt..."
        />
      </div>
      
      <div>
        <label>Tên người liên hệ</label>
        <input
          type="text"
          name="contactName"
          value={formData.contactName}
          onChange={handleInputChange}
        />
      </div>
      
      <div>
        <label>Số điện thoại</label>
        <input
          type="tel"
          name="contactPhone"
          value={formData.contactPhone}
          onChange={handleInputChange}
        />
      </div>
      
      <div>
        <label>Email</label>
        <input
          type="email"
          name="contactEmail"
          value={formData.contactEmail}
          onChange={handleInputChange}
        />
      </div>
      
      <div>
        <label>Ảnh hiện tại</label>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '10px' }}>
          {imagesToKeep.map((url, index) => (
            <div key={index} style={{ position: 'relative' }}>
              <img src={url} alt={`Image ${index}`} style={{ width: '100px', height: '100px', objectFit: 'cover' }} />
              <button
                type="button"
                onClick={() => handleRemoveImage(url)}
                style={{ position: 'absolute', top: 0, right: 0 }}
              >
                X
              </button>
            </div>
          ))}
        </div>
      </div>
      
      <div>
        <label>Thêm ảnh mới</label>
        <input
          type="file"
          multiple
          accept="image/*"
          onChange={handleImageChange}
        />
        {newImages.length > 0 && <p>{newImages.length} ảnh mới đã chọn</p>}
      </div>
      
      <button type="submit" disabled={loading}>
        {loading ? 'Đang cập nhật...' : 'Cập nhật'}
      </button>
    </form>
  );
};

export default UpdateHostelForm;
```

---

## Testing với Postman

### Test API Update Images

1. **Method**: PUT
2. **URL**: `http://localhost:8080/api/hostels/1/images`
3. **Authorization**: Bearer Token
4. **Body** (form-data):
   - Key: `keepImages`, Value: `https://res.cloudinary.com/.../old1.jpg`
   - Key: `imageFiles`, Type: File, Value: Select image file
   - Key: `imageFiles`, Type: File, Value: Select another image file

### Test API Update Hostel Info

1. **Method**: PUT
2. **URL**: `http://localhost:8080/api/hostels/1`
3. **Authorization**: Bearer Token
4. **Body** (raw JSON):
```json
{
  "name": "Nhà trọ test update",
  "address": "123 Test Street",
  "description": "Mô tả update",
  "price": 3500000,
  "area": 25,
  "amenities": "Wifi, AC",
  "contactName": "John Doe",
  "contactPhone": "0901234567",
  "contactEmail": "john@example.com",
  "images": [
    "https://res.cloudinary.com/.../image1.jpg",
    "https://res.cloudinary.com/.../image2.jpg"
  ]
}
```

---

## Best Practices

1. **Validation**: Validate dữ liệu ở client trước khi gửi lên server
2. **Loading State**: Hiển thị loading indicator khi đang xử lý
3. **Error Handling**: Xử lý lỗi và hiển thị message rõ ràng cho user
4. **Image Preview**: Cho phép user preview ảnh trước khi upload
5. **Progress Bar**: Hiển thị tiến trình upload ảnh (đặc biệt với nhiều ảnh)
6. **Optimistic UI**: Cập nhật UI ngay lập tức, rollback nếu API fail
7. **Image Compression**: Nén ảnh ở client trước khi upload để tối ưu bandwidth
