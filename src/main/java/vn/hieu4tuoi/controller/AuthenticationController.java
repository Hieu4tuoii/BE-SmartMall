package vn.hieu4tuoi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hieu4tuoi.dto.request.auth.ConfirmOtpRequest;
import vn.hieu4tuoi.dto.request.auth.RegisterInformationRequest;
import vn.hieu4tuoi.dto.request.auth.SignInRequest;
import vn.hieu4tuoi.dto.request.auth.SignUpRequest;
import vn.hieu4tuoi.dto.respone.ResponseData;
import vn.hieu4tuoi.service.AuthService;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication Controller")
@Slf4j(topic = "AUTHENTICATION-CONTROLLER")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthService authService;

    @PostMapping("/sign-in")
    public ResponseData<?> login(@Valid @RequestBody SignInRequest loginRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "Login successfully", authService.login(loginRequest));
    }

    @PostMapping("/sign-up")
    public ResponseData<?> register(@Valid @RequestBody SignUpRequest signUpRequest) throws IOException {
        String userId = authService.register(signUpRequest);
        return new ResponseData<>(HttpStatus.CREATED.value(), "Đăng ký thành công", userId);
    }

    @PostMapping("/confirm-otp")
    public ResponseData<?> confirmOtp(@Valid @RequestBody ConfirmOtpRequest confirmOtpRequest) {
        authService.confirmOTP(confirmOtpRequest);
        return new ResponseData<>(HttpStatus.OK.value(), "Xác thực OTP thành công.");
    }

    @PostMapping("/register-information")
    public ResponseData<?> registerInformation(@Valid @RequestBody RegisterInformationRequest request) {
        authService.registerInformation(request);
        return new ResponseData<>(HttpStatus.OK.value(), "Hoàn tất đăng ký thành công");
    }
}
