package vn.hieu4tuoi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
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

    //hiện thực hàm loaduser
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndIsDeletedFalse(username);
        if(user==null){
            throw  new UsernameNotFoundException("Tài khoản không tồn tại");
        }
        //nhaajn vao username, pw, list grandtedauthoriry tra ve 1 userdetail
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), rolesToAuthorities(user.getAuthorities()));
    }

    //chuyen doi ds authorities entity thành ds grandedauthority
    private Collection<? extends GrantedAuthority> rolesToAuthorities(Collection<Authorities> authorities){
        //chuyển đổi từng đối tượng Authorities trong Stream thành một đối tượng SimpleGrantedAuthority.
        return authorities.stream().map(authority -> new SimpleGrantedAuthority(authority.getAuthoritiesID())).collect(Collectors.toList());
    }
}
