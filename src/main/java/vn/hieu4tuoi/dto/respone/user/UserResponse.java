package vn.hieu4tuoi.dto.respone.user;

import lombok.Getter;
import lombok.Setter;
import vn.hieu4tuoi.common.StepActive;
import vn.hieu4tuoi.common.UserStatus;

@Getter
@Setter
public class UserResponse {
    private String id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private StepActive stepActive;
    private UserStatus status;
}
