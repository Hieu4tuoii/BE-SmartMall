# Hướng Dẫn Sử Dụng SecurityContext

## Tổng Quan

Hệ thống đã được cấu hình để tự động lưu thông tin user vào SecurityContext khi xác thực JWT thành công. Điều này cho phép bạn truy xuất thông tin user ở bất kỳ đâu trong ứng dụng mà không cần truyền userId qua các tham số.

## Các Class Đã Được Tạo

### 1. `CustomUserDetails`
Location: `src/main/java/vn/hieu4tuoi/Security/CustomUserDetails.java`

Class này implement `UserDetails` của Spring Security và lưu trữ đầy đủ thông tin user:
- `id` - ID của user
- `email` - Email của user
- `password` - Mật khẩu đã mã hóa
- `fullName` - Tên đầy đủ
- `phoneNumber` - Số điện thoại
- `address` - Địa chỉ
- `status` - Trạng thái tài khoản
- `authorities` - Danh sách quyền

### 2. `SecurityUtils`
Location: `src/main/java/vn/hieu4tuoi/Security/SecurityUtils.java`

Class tiện ích cung cấp các phương thức static để truy xuất thông tin user từ SecurityContext:

#### Các Phương Thức Chính:

```java
// Lấy thông tin user hiện tại (CustomUserDetails)
CustomUserDetails user = SecurityUtils.getCurrentUser();

// Lấy ID của user hiện tại
String userId = SecurityUtils.getCurrentUserId();

// Lấy email của user hiện tại
String email = SecurityUtils.getCurrentUserEmail();

// Lấy tên đầy đủ
String fullName = SecurityUtils.getCurrentUserFullName();

// Lấy số điện thoại
String phoneNumber = SecurityUtils.getCurrentUserPhoneNumber();

// Lấy địa chỉ
String address = SecurityUtils.getCurrentUserAddress();

// Lấy trạng thái
UserStatus status = SecurityUtils.getCurrentUserStatus();

// Kiểm tra xem user có đăng nhập không
boolean isAuthenticated = SecurityUtils.isAuthenticated();

// Lấy username dưới dạng Optional
Optional<String> username = SecurityUtils.getCurrentUsername();
```

## Cách Sử Dụng Trong Service

### Ví Dụ 1: Trong CartServiceImpl

```java
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    
    @Override
    public String addToCart(String productColorVersionId, Integer quantity) {
        // Lấy userId từ SecurityContext
        String currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập");
        }
        
        // Sử dụng userId để xử lý logic
        // ...
    }
}
```

### Ví Dụ 2: Trong OrderService

```java
@Service
public class OrderServiceImpl implements OrderService {
    
    @Override
    public Order createOrder(CreateOrderRequest request) {
        // Lấy thông tin user hiện tại
        String userId = SecurityUtils.getCurrentUserId();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        String userFullName = SecurityUtils.getCurrentUserFullName();
        
        // Tạo đơn hàng với thông tin user
        Order order = new Order();
        order.setUserId(userId);
        order.setCustomerEmail(userEmail);
        order.setCustomerName(userFullName);
        
        // ...
        return order;
    }
}
```

### Ví Dụ 3: Trong Controller

```java
@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserProfile() {
        // Lấy thông tin user hiện tại
        CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập");
        }
        
        // Trả về thông tin user
        UserResponse response = new UserResponse();
        response.setId(currentUser.getId());
        response.setEmail(currentUser.getEmail());
        response.setFullName(currentUser.getFullName());
        response.setPhoneNumber(currentUser.getPhoneNumber());
        response.setAddress(currentUser.getAddress());
        
        return ResponseEntity.ok(response);
    }
}
```

## Luồng Hoạt Động

1. **Client gửi request** với JWT token trong header Authorization
2. **CustomizeRequestFilter** bắt request và:
   - Extract token từ header
   - Validate token
   - Gọi `UserSecurityService.loadUserByUsername()`
3. **UserSecurityServiceImpl** trả về `CustomUserDetails` với đầy đủ thông tin user
4. **Filter** lưu `CustomUserDetails` vào `SecurityContext`
5. **Bất kỳ Service/Controller nào** có thể lấy thông tin user thông qua `SecurityUtils`

## Lưu Ý

1. **Không cần truyền userId qua parameter**: Với SecurityContext, bạn có thể lấy userId trực tiếp từ token, không cần client gửi userId trong request body.

2. **Kiểm tra null**: Luôn kiểm tra null khi lấy thông tin từ SecurityUtils, vì nếu user chưa đăng nhập, các phương thức sẽ trả về null.

3. **Thread-safe**: SecurityContext được lưu trong ThreadLocal, nên mỗi request sẽ có context riêng, hoàn toàn thread-safe.

4. **Performance**: Thông tin user chỉ được load một lần khi xác thực JWT, sau đó được lưu trong context cho toàn bộ request.

## Best Practices

1. **Luôn validate authentication**:
```java
if (!SecurityUtils.isAuthenticated()) {
    throw new UnauthorizedException("Vui lòng đăng nhập");
}
```

2. **Sử dụng Optional khi cần**:
```java
String username = SecurityUtils.getCurrentUsername()
    .orElseThrow(() -> new UnauthorizedException("Vui lòng đăng nhập"));
```

3. **Log thông tin user cho debugging**:
```java
log.info("User {} ({}) is accessing resource", 
    SecurityUtils.getCurrentUserFullName(), 
    SecurityUtils.getCurrentUserId());
```

4. **Không lưu trữ reference**: Không lưu CustomUserDetails vào biến instance/static, luôn lấy mới từ SecurityUtils.

## Các File Đã Sửa Đổi

1. **CustomUserDetails.java** - Class lưu thông tin user
2. **SecurityUtils.java** - Utility class để truy xuất thông tin
3. **UserSecurityServiceImpl.java** - Cập nhật để trả về CustomUserDetails
4. **CartServiceImpl.java** - Ví dụ sử dụng SecurityUtils

## Tài Liệu Tham Khảo

- Spring Security Documentation: https://docs.spring.io/spring-security/reference/
- SecurityContextHolder: https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/core/context/SecurityContextHolder.html

