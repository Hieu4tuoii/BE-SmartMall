package vn.hieu4tuoi.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.hieu4tuoi.common.StepActive;
import vn.hieu4tuoi.common.UserStatus;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User extends AbstractEntity  {

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "address", length = 500)
    private String address;
    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH
            }
    )
    @JoinTable(name = "authorities_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name ="authorities_id")
    )
    private List<Authorities> authorities;
    @Column(name = "step_active")
    //các buocs trong qua trình dang ky
    private StepActive stepActive;
    @Column(name = "otp", length = 6)
    private String OTP;


    @Column(name = "status", length = 50)
    private UserStatus status;

    //mac dinh user tao ra se có status la NONE
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (status == null) {
            status = UserStatus.NONE;
        }
    }
//
//
//    @Column(name = "role_id")
//    private String roleId;

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of();
//    }
//
//    @Override
//    public String getUsername() {
//        return email;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return "ACTIVE".equals(status);
//    }
}
