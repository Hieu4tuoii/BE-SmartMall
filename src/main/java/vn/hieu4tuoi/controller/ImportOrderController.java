package vn.hieu4tuoi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import vn.hieu4tuoi.dto.request.importOrder.ImportOrderRequest;
import vn.hieu4tuoi.dto.respone.ResponseData;
import vn.hieu4tuoi.service.ImportOrderService;

@RestController
@RequestMapping("/import-order")
@Tag(name = "Import Order Controller")
@RequiredArgsConstructor
@Validated
public class ImportOrderController {
    private final ImportOrderService importOrderService;

    @PostMapping
    public ResponseData<String> create(@RequestBody ImportOrderRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Tạo đơn nhập hàng thành công", importOrderService.create(request));
    }
}
