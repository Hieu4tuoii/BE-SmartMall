package vn.hieu4tuoi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import vn.hieu4tuoi.dto.request.order.OrderRequest;
import vn.hieu4tuoi.dto.respone.ResponseData;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.respone.order.OrderDetailResponse;
import vn.hieu4tuoi.dto.respone.order.OrderResponse;
import vn.hieu4tuoi.service.OrderService;
import vn.hieu4tuoi.common.OrderStatus;

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

    @GetMapping("/list")
    public ResponseData<PageResponse<List<OrderResponse>>> getOrderList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "modifiedAt:desc") String sort, @RequestParam(defaultValue = "") String keyword, @RequestParam(defaultValue = "PENDING") OrderStatus status) {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách đơn hàng thành công", orderService.getOrderList(page, size, sort, keyword, status));
    }

    @GetMapping("/detail/{id}")
    public ResponseData<OrderDetailResponse> getOrderDetail(@PathVariable String id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy chi tiết đơn hàng thành công", orderService.getOrderDetail(id));
    }
}
