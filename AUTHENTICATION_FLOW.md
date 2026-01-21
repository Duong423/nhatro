# Luồng Phân Quyền và Xác Thực Trong Hệ Thống

## 🔐 Flow Đăng Nhập (Login)

### 1. User gửi request login
```http
POST /api/auth/login
{
    "email": "user@example.com",
    "password": "123456"
}
```

### 2. AuthServiceImpl.login() xử lý
```java
// Bước 1: Spring Security xác thực email + password
authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(email, password)
);
// → Gọi UserDetailsService.loadUserByUsername()
// → Query DB: SELECT * FROM users WHERE email = ?
// → So sánh password (BCrypt)
// → Nếu sai → throw BadCredentialsException
// → Nếu đúng → tiếp tục

// Bước 2: Lấy user từ DB (bao gồm role)
User user = userRepository.findByEmail(email);
// user.getRole() = ADMIN / OWNER / TENANT

// Bước 3: Tạo JWT token (NHÚNG ROLE VÀO TOKEN)
UserDetails userDetails = userDetailsService.loadUserByUsername(email);
String accessToken = jwtUtil.generateToken(userDetails);
// JWT payload: { "sub": "user@example.com", "role": "ROLE_ADMIN", "exp": ... }

// Bước 4: Lưu token vào DB (optional - để revoke)
user.setAccessToken(accessToken);
userRepository.save(user);

// Bước 5: Trả về response
return AuthResponse.builder()
    .accessToken(accessToken)
    .role(user.getRole())  // Trả về role cho frontend
    .build();
```

**✅ Kết luận**: 
- Login chỉ cần đúng email + password (KHÔNG kiểm tra role)
- Role được lấy từ DB và nhúng vào JWT token
- Tất cả users (ADMIN, OWNER, TENANT) đều login được

---

## 🔑 Flow Gọi API Có Phân Quyền

### 1. Frontend gửi request với token
```http
GET /api/admin/users
Headers:
    Authorization: Bearer eyJhbGc...
```

### 2. JwtAuthenticationFilter xử lý
```java
// Bước 1: Lấy JWT từ header
String jwt = authHeader.substring(7);

// Bước 2: Parse JWT và lấy thông tin (KHÔNG QUERY DB)
String userEmail = jwtUtil.extractUsername(jwt);  // từ JWT payload
String role = jwtUtil.extractRole(jwt);          // từ JWT payload
// role = "ROLE_ADMIN" hoặc "ROLE_OWNER" hoặc "ROLE_TENANT"

// Bước 3: Validate JWT signature
if (jwtUtil.validateToken(jwt, userDetails)) {
    // JWT hợp lệ và chưa hết hạn
}

// Bước 4: Tạo Authentication object với role
UserDetails userDetails = User.builder()
    .username(userEmail)
    .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
    .build();

UsernamePasswordAuthenticationToken authToken = 
    new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities()
    );

// Bước 5: Set vào SecurityContext
SecurityContextHolder.getContext().setAuthentication(authToken);
```

### 3. Spring Security kiểm tra quyền
```java
// SecurityConfig đã cấu hình:
.requestMatchers("/api/admin/**").hasRole("ADMIN")

// Spring Security tự động check:
if (authToken.getAuthorities().contains("ROLE_ADMIN")) {
    // ✅ Cho phép truy cập
} else {
    // ❌ Trả về 403 Forbidden
}
```

**✅ Kết luận**:
- Mỗi request API chỉ parse JWT token (KHÔNG query DB)
- Role được lấy trực tiếp từ JWT payload
- Performance tốt hơn (không cần query DB mỗi request)

---

## 🎯 So Sánh 2 Cách Kiểm Tra Role

### ❌ Cách CŨ (Chậm)
```
Request → Parse JWT → Query DB (SELECT role FROM users) → Check role → Response
         ↑____________Query DB mỗi request!____________↑
```

### ✅ Cách MỚI (Nhanh - Đã implement)
```
Request → Parse JWT (role có sẵn trong token) → Check role → Response
         ↑____________Không cần DB!____________↑
```

---

## ⚠️ Trade-off: JWT chứa role

### ✅ Ưu điểm:
- Performance cao (không query DB mỗi request)
- Giảm tải database
- Stateless hoàn toàn

### ❌ Nhược điểm:
- Nếu admin đổi role user, JWT cũ vẫn có role cũ cho đến khi hết hạn (24h)
- **Giải pháp**: Revoke token khi đổi role

---

## 🔄 Khi Nào Role Được Cập Nhật?

### Trường hợp 1: Admin đổi role user
```java
// Admin gọi API
PUT /api/admin/users/role
{
    "userId": 5,
    "role": "OWNER"
}

// Backend cập nhật DB
user.setRole(UserRole.OWNER);
userRepository.save(user);

// ⚠️ JWT token cũ vẫn có role TENANT!
// User phải logout và login lại để lấy JWT mới với role OWNER
```

### Trường hợp 2: Revoke token khi đổi role (Recommended)
```java
@Transactional
public User updateUserRole(Long userId, UserRole role) {
    User user = getUserById(userId);
    user.setRole(role);
    
    // Revoke tất cả token cũ
    user.setAccessToken(null);
    user.setRefreshToken(null);
    
    // Hoặc dùng token blacklist
    tokenBlacklistService.revokeAllUserTokens(userId);
    
    return userRepository.save(user);
}
```

---

## 📊 Tóm Tắt

| Thời điểm | Role được lấy từ | Note |
|-----------|-----------------|------|
| **Đăng nhập** | DB (`users.role`) | Role được nhúng vào JWT |
| **Mỗi request API** | JWT token payload | Không query DB |
| **Đổi role** | Cập nhật DB | User phải login lại |

---

## 🚀 Cách Test

### 1. Đăng ký user mới
```bash
POST /api/auth/register
→ Tạo user với role = TENANT (mặc định)
→ Nhận JWT token có role = "ROLE_TENANT"
```

### 2. Thử truy cập admin endpoint
```bash
GET /api/admin/users
Headers: Authorization: Bearer {tenant_token}
→ 403 Forbidden (vì role = TENANT, không phải ADMIN)
```

### 3. Admin đổi role user lên OWNER
```bash
PUT /api/admin/users/role
{
    "userId": 2,
    "role": "OWNER"
}
→ DB: users.role = 'OWNER'
```

### 4. User login lại
```bash
POST /api/auth/login
→ JWT mới có role = "ROLE_OWNER"
```

### 5. Truy cập owner endpoint
```bash
GET /api/owner/hostels
Headers: Authorization: Bearer {new_owner_token}
→ 200 OK ✅
```

---

## 🔐 Bảo Mật

### 1. JWT Secret phải đủ dài
```properties
jwt.secret=yourSecretKeyForJWTTokenGenerationMustBeLongEnoughForHS512Algorithm
# Tối thiểu 64 ký tự cho HS512
```

### 2. Token expiration
```properties
jwt.expiration=86400000        # 24 giờ
jwt.refresh-expiration=604800000  # 7 ngày
```

### 3. HTTPS bắt buộc trong production
- JWT token dễ bị đánh cắp nếu dùng HTTP
- Luôn dùng HTTPS

### 4. Không lưu password trong JWT
- ✅ Đã implement đúng
- JWT chỉ chứa: email, role, exp, iat
