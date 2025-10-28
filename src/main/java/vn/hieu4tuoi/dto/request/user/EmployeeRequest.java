package vn.hieu4tuoi.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequest {
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String imageUrl;
    private String authoritiesId;
}
