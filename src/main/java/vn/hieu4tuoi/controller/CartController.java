package vn.hieu4tuoi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import vn.hieu4tuoi.dto.request.cart.UpdateCartItemRequest;
import vn.hieu4tuoi.dto.respone.ResponseData;
import vn.hieu4tuoi.dto.respone.cart.CartResponse;
import vn.hieu4tuoi.service.CartService;

@RestController
@RequestMapping("/cart")
@Tag(name = "Cart Controller")
@RequiredArgsConstructor
@Validated
public class CartController {
    private final CartService cartService;

    @PutMapping("/update")
    public ResponseData<?> updateCartItem(@RequestBody @Valid UpdateCartItemRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Cập nhật giỏ hàng thành công", cartService.updateCartItem(request));
    }

    @GetMapping("/count")
    public ResponseData<Integer> getCartItemCount() {
        return new ResponseData<Integer>(HttpStatus.OK.value(), "Lấy số lượng sản phẩm trong giỏ hàng thành công", cartService.getCartItemCount());
    }

    @GetMapping("/detail")
    public ResponseData<CartResponse> getCartDetail() {
        return new ResponseData<CartResponse>(HttpStatus.OK.value(), "Lấy thông tin giỏ hàng thành công", cartService.getCartDetail());
    }
}
