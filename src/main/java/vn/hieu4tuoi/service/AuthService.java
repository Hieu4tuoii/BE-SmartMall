package vn.hieu4tuoi.service;

import vn.hieu4tuoi.Security.JwtRespone;
import vn.hieu4tuoi.dto.request.auth.ConfirmOtpRequest;
import vn.hieu4tuoi.dto.request.auth.RegisterInformationRequest;
import vn.hieu4tuoi.dto.request.auth.SignInRequest;
import vn.hieu4tuoi.dto.request.auth.SignUpRequest;

import java.io.IOException;


public interface AuthService {
    JwtRespone login(SignInRequest request);
    String register(SignUpRequest request) throws IOException;
    void registerInformation(RegisterInformationRequest request);
    void confirmOTP(ConfirmOtpRequest OTP);
}
