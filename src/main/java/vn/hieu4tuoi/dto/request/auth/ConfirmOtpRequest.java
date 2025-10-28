package vn.hieu4tuoi.dto.request.auth;

import lombok.Getter;

@Getter
public class ConfirmOtpRequest {
    private String id;
    private String otp;
}
