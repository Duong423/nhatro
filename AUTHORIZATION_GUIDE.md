# Hướng Dẫn Phân Quyền Hệ Thống Quản Lý Nhà Trọ

## 🔐 Các Role trong hệ thống

### 1. **ADMIN** - Quản trị viên
- Quyền cao nhất trong hệ thống
- Quản lý tất cả users
- Thay đổi role của users
- Xóa users
- Truy cập tất cả tài nguyên

### 2. **OWNER** - Chủ nhà trọ
- Quản lý nhà trọ của mình
- Quản lý phòng trọ
- Quản lý hợp đồng
- Quản lý tenant trong nhà trọ của mình
- Xem báo cáo, thống kê

### 3. **TENANT** - Người thuê
- Xem thông tin phòng của mình
- Xem hợp đồng
- Xem hóa đơn
- Gửi yêu cầu bảo trì

---

## 📋 API Endpoints theo Role

### Public Endpoints (Không cần đăng nhập)
```
POST /api/auth/register        - Đăng ký tài khoản
POST /api/auth/login           - Đăng nhập
POST /api/auth/refresh-token   - Làm mới token
```

### Admin Endpoints (Chỉ ADMIN)
```
GET    /api/admin/dashboard         - Xem dashboard admin
GET    /api/admin/users             - Danh sách tất cả users
GET    /api/admin/users/{id}        - Chi tiết user
PUT    /api/admin/users/role        - Cập nhật role user
DELETE /api/admin/users/{id}        - Xóa user
GET    /api/admin/profile           - Xem profile của mình
```

### Owner Endpoints (ADMIN + OWNER)
```
GET /api/owner/dashboard   - Dashboard chủ nhà trọ
GET /api/owner/hostels     - Danh sách nhà trọ
GET /api/owner/profile     - Xem profile
```

### Tenant Endpoints (ADMIN + OWNER + TENANT)
```
GET /api/tenant/dashboard   - Dashboard người thuê
GET /api/tenant/rooms       - Phòng của mình
GET /api/tenant/contracts   - Hợp đồng
GET /api/tenant/profile     - Xem profile
```

---

## 🔧 Cách sử dụng phân quyền trong code

### 1. Sử dụng annotation `@PreAuthorize`
```java
@GetMapping("/admin-only")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> adminOnly() {
    return ResponseEntity.ok("Chỉ admin mới thấy");
}

@GetMapping("/owner-or-admin")
@PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
public ResponseEntity<?> ownerOrAdmin() {
    return ResponseEntity.ok("Admin hoặc Owner thấy được");
}
```

### 2. Sử dụng custom annotations
```java
@GetMapping("/admin-endpoint")
@IsAdmin
public ResponseEntity<?> adminEndpoint() {
    return ResponseEntity.ok("Chỉ admin");
}

@GetMapping("/owner-endpoint")
@IsOwner  // ADMIN hoặc OWNER
public ResponseEntity<?> ownerEndpoint() {
    return ResponseEntity.ok("Admin hoặc Owner");
}

@GetMapping("/authenticated")
@IsAuthenticated  // Bất kỳ user đã đăng nhập
public ResponseEntity<?> authenticatedEndpoint() {
    return ResponseEntity.ok("User đã đăng nhập");
}
```

### 3. Kiểm tra role trong code
```java
@GetMapping("/check-role")
public ResponseEntity<?> checkRole(Principal principal) {
    User user = userService.getCurrentUser(principal.getName());
    
    if (user.getRole() == UserRole.ADMIN) {
        // Logic cho admin
    } else if (user.getRole() == UserRole.OWNER) {
        // Logic cho owner
    } else {
        // Logic cho tenant
    }
    
    return ResponseEntity.ok(user);
}
```

---

## 🧪 Test API với Postman

### 1. Đăng ký user mới (mặc định là TENANT)
```bash
POST http://localhost:8080/api/auth/register
{
    "fullName": "Nguyen Van A",
    "email": "user@example.com",
    "password": "123456",
    "phone": "0123456789"
}
```

### 2. Đăng nhập
```bash
POST http://localhost:8080/api/auth/login
{
    "email": "user@example.com",
    "password": "123456"
}

Response:
{
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc...",
    "tokenType": "Bearer",
    "userId": 1,
    "email": "user@example.com",
    "fullName": "Nguyen Van A",
    "role": "TENANT"
}
```

### 3. Gọi API có phân quyền (thêm header)
```bash
GET http://localhost:8080/api/tenant/dashboard
Headers:
    Authorization: Bearer eyJhbGc...
```

### 4. Admin đổi role của user (chỉ ADMIN)
```bash
PUT http://localhost:8080/api/admin/users/role
Headers:
    Authorization: Bearer {admin_token}
Body:
{
    "userId": 2,
    "role": "OWNER"
}
```

---

## 🎯 Luồng phân quyền

1. **User đăng ký** → Role mặc định: `TENANT`
2. **Admin đăng nhập** → Có quyền thay đổi role của users khác
3. **Admin nâng user lên OWNER** → User đó có quyền quản lý nhà trọ
4. **Mỗi endpoint check role** → Trả về 403 Forbidden nếu không đủ quyền

---

## ⚠️ Lưu ý

1. **Tạo admin đầu tiên**: 
   - Đăng ký user bình thường
   - Vào DB thủ công đổi role thành ADMIN
   ```sql
   UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
   ```

2. **Token hết hạn**: 
   - Access token: 24h
   - Refresh token: 7 ngày
   - Dùng refresh token để lấy access token mới

3. **Lỗi 403 Forbidden**: User không có quyền truy cập endpoint đó

4. **Lỗi 401 Unauthorized**: Token không hợp lệ hoặc chưa đăng nhập

---

## 📝 TODO

- [ ] Thêm permission chi tiết hơn (CRUD permissions)
- [ ] Implement row-level security (user chỉ xem data của mình)
- [ ] Thêm audit log (ghi lại ai làm gì, khi nào)
- [ ] Implement IP whitelist cho admin
- [ ] Two-factor authentication (2FA)
