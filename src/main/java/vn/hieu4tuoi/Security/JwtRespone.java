
package vn.hieu4tuoi.Security;

import lombok.*;
import vn.hieu4tuoi.common.StepActive;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class JwtRespone {
    private String id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private StepActive stepActive;
    private String jwt;
}
