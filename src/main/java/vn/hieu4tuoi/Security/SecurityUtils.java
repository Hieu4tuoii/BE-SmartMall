package vn.hieu4tuoi.Security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.hieu4tuoi.common.UserStatus;

import java.util.Optional;

/**
 * Utility class để truy xuất thông tin user từ SecurityContext một cách dễ dàng
 */
public class SecurityUtils {

    /**
     * Lấy thông tin user hiện tại từ SecurityContext
     * @return CustomUserDetails của user đang đăng nhập, hoặc null nếu chưa đăng nhập
     */
    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Lấy ID của user hiện tại
     * @return ID của user, hoặc null nếu chưa đăng nhập
     */
    public static String getCurrentUserId() {
        CustomUserDetails user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    /**
     * Lấy email của user hiện tại
     * @return Email của user, hoặc null nếu chưa đăng nhập
     */
    public static String getCurrentUserEmail() {
        CustomUserDetails user = getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    /**
     * Lấy tên đầy đủ của user hiện tại
     * @return Tên đầy đủ của user, hoặc null nếu chưa đăng nhập
     */
    public static String getCurrentUserFullName() {
        CustomUserDetails user = getCurrentUser();
        return user != null ? user.getFullName() : null;
    }

    /**
     * Lấy số điện thoại của user hiện tại
     * @return Số điện thoại của user, hoặc null nếu chưa đăng nhập
     */
    public static String getCurrentUserPhoneNumber() {
        CustomUserDetails user = getCurrentUser();
        return user != null ? user.getPhoneNumber() : null;
    }

    /**
     * Lấy địa chỉ của user hiện tại
     * @return Địa chỉ của user, hoặc null nếu chưa đăng nhập
     */
    public static String getCurrentUserAddress() {
        CustomUserDetails user = getCurrentUser();
        return user != null ? user.getAddress() : null;
    }

    /**
     * Lấy trạng thái của user hiện tại
     * @return Trạng thái của user, hoặc null nếu chưa đăng nhập
     */
    public static UserStatus getCurrentUserStatus() {
        CustomUserDetails user = getCurrentUser();
        return user != null ? user.getStatus() : null;
    }

    /**
     * Kiểm tra xem user hiện tại có đang đăng nhập không
     * @return true nếu đã đăng nhập, false nếu chưa
     */
    public static boolean isAuthenticated() {
        return getCurrentUser() != null;
    }

    /**
     * Lấy username (email) của user hiện tại dưới dạng Optional
     * @return Optional chứa username, hoặc empty nếu chưa đăng nhập
     */
    public static Optional<String> getCurrentUsername() {
        return Optional.ofNullable(getCurrentUserEmail());
    }
}

