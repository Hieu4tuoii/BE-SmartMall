package vn.hieu4tuoi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.hieu4tuoi.Security.CustomUserDetails;
import vn.hieu4tuoi.model.Authorities;
import vn.hieu4tuoi.model.User;
import vn.hieu4tuoi.repository.UserRepository;
import vn.hieu4tuoi.service.UserSecurityService;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserSecurityServiceImpl implements UserSecurityService {
    private UserRepository userRepository;

    @Autowired
    public UserSecurityServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load thông tin user theo username (email) và trả về CustomUserDetails
     * CustomUserDetails chứa đầy đủ thông tin user để lưu vào SecurityContext
     * 
     * @param username Email của user
     * @return CustomUserDetails chứa thông tin đầy đủ của user
     * @throws UsernameNotFoundException nếu không tìm thấy user
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndIsDeletedFalse(username);
        if(user==null){
            throw  new UsernameNotFoundException("Tài khoản không tồn tại");
        }
        
        // Tạo CustomUserDetails với đầy đủ thông tin user
        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getStatus(),
                rolesToAuthorities(user.getAuthorities())
        );
    }

    //chuyen doi ds authorities entity thành ds grandedauthority
    private Collection<? extends GrantedAuthority> rolesToAuthorities(Collection<Authorities> authorities){
        //chuyển đổi từng đối tượng Authorities trong Stream thành một đối tượng SimpleGrantedAuthority.
        return authorities.stream().map(authority -> new SimpleGrantedAuthority(authority.getAuthoritiesID())).collect(Collectors.toList());
    }
}
