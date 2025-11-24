package vn.hieu4tuoi.controller;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
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
import vn.hieu4tuoi.service.BankService;

import java.io.IOException;

@RestController
@RequestMapping("/api/bank")
@Tag(name = "Bank Controller")
@Slf4j(topic = "BANK-CONTROLLER")
@RequiredArgsConstructor
public class BankController {
    private final BankService bankService;

    @PostMapping("/check/{orderId}")
    public ResponseData<?> checkPayment(@PathVariable String orderId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Check payment successfully", bankService.isValidBank(orderId));
    }
}
