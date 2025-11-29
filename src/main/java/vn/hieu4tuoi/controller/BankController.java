package vn.hieu4tuoi.controller;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hieu4tuoi.dto.respone.ResponseData;
import vn.hieu4tuoi.service.BankService;


@RestController
@RequestMapping("/bank")
@Tag(name = "Bank Controller")
@Slf4j(topic = "BANK-CONTROLLER")
@RequiredArgsConstructor
public class BankController {
    private final BankService bankService;

    @GetMapping("/check/{orderId}")
    public ResponseData<?> checkPayment(@PathVariable String orderId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Check payment successfully", bankService.isValidBank(orderId));
    }
}
