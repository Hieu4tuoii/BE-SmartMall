package vn.hieu4tuoi.Security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.hieu4tuoi.common.UserStatus;

import java.util.Collection;

/**
 * Custom UserDetails implementation để lưu thông tin đầy đủ của user vào SecurityContext
 * Khi xác thực JWT, các thông tin này sẽ được lưu và có thể truy xuất từ SecurityContextHolder
 */
@Getter
@Setter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    
    private String id;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String address;
    private UserStatus status;
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Trả về danh sách quyền của user
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Trả về password của user
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Trả về username (trong trường hợp này là email)
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Kiểm tra tài khoản có hết hạn không
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Kiểm tra tài khoản có bị khóa không
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Kiểm tra thông tin xác thực có hết hạn không
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Kiểm tra tài khoản có được kích hoạt không
     */
    @Override
    public boolean isEnabled() {
        return UserStatus.ACTIVE.equals(status);
    }
}

