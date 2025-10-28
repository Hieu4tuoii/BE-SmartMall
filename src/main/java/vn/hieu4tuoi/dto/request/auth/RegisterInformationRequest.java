package vn.hieu4tuoi.dto.request.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hieu4tuoi.common.StepActive;
import vn.hieu4tuoi.model.Authorities;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterInformationRequest {
    private String id;
    private String fullName;
    private String phoneNumber;
    private String address;
}
