package vn.hieu4tuoi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import vn.hieu4tuoi.dto.request.order.OrderRequest;
import vn.hieu4tuoi.dto.respone.ResponseData;
import vn.hieu4tuoi.service.OrderService;

@RestController
@RequestMapping("/order")
@Tag(name = "Order Controller")
@RequiredArgsConstructor
@Validated
public class OrderController {
    private final OrderService orderService;
    @PostMapping("/create")
    public ResponseData<?> createOrder(@RequestBody @Valid OrderRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Tạo đơn hàng thành công", orderService.createOrder(request));
    }
}
