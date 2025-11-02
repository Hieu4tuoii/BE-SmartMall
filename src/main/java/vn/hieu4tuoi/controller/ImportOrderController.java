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
import vn.hieu4tuoi.dto.request.importOrder.ProductImportSelectRequest;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.respone.ResponseData;
import vn.hieu4tuoi.dto.respone.importOder.ImportOrderDetailResponse;
import vn.hieu4tuoi.dto.respone.importOder.ImportOrderResponse;
import vn.hieu4tuoi.dto.respone.importOder.ProductImportSelectResponse;
import vn.hieu4tuoi.service.ImportOrderService;

import java.util.List;

@RestController
@RequestMapping("/import-order")
@Tag(name = "Import Order Controller")
@RequiredArgsConstructor
@Validated
public class ImportOrderController {
    private final ImportOrderService importOrderService;

    @PostMapping
    public ResponseData<String> create(@Valid @RequestBody ImportOrderRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Tạo đơn nhập hàng thành công", importOrderService.create(request));
    }

    @GetMapping
    public ResponseData<PageResponse<List<ImportOrderResponse>>> getListImportOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "modifiedAt:desc") String sort) {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách đơn nhập hàng thành công",
                importOrderService.getListImportOrders(page, size, sort));
    }

    @PostMapping("/product-import-select")
    public ResponseData<List<ProductImportSelectResponse>> getProductImportSelectList(@Valid @RequestBody ProductImportSelectRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách sản phẩm để chọn thành công",
                importOrderService.getProductImportSelectList(request));
    }

    @GetMapping("/{id}")
    public ResponseData<ImportOrderDetailResponse> getById(@PathVariable String id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy thông tin đơn nhập thành công",
                importOrderService.getById(id));
    }
}
