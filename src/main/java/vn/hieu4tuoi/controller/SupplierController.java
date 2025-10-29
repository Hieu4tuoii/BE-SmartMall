package vn.hieu4tuoi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import vn.hieu4tuoi.dto.request.supplier.SupplierRequest;
import vn.hieu4tuoi.dto.respone.ResponseData;
import vn.hieu4tuoi.dto.respone.SupplierResponse;
import vn.hieu4tuoi.service.SupplierService;

import java.util.List;

@RestController
@RequestMapping("/supplier")
@Tag(name = "Supplier Controller")
@RequiredArgsConstructor
@Validated
@Slf4j(topic = "SUPPLIER-CONTROLLER")
public class SupplierController {
    
    private final SupplierService supplierService;

    @PostMapping
    @Operation(summary = "Tạo mới nhà cung cấp")
    public ResponseData<?> create(@RequestBody @Valid SupplierRequest request) {
        log.info("API tạo mới nhà cung cấp được gọi");
        String supplierId = supplierService.create(request);
        return new ResponseData<>(HttpStatus.CREATED.value(), "Tạo nhà cung cấp thành công", supplierId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin nhà cung cấp")
    public ResponseData<?> update(@PathVariable String id, @RequestBody @Valid SupplierRequest request) {
        log.info("API cập nhật nhà cung cấp với ID: {} được gọi", id);
        String supplierId = supplierService.update(id, request);
        return new ResponseData<>(HttpStatus.OK.value(), "Cập nhật nhà cung cấp thành công", supplierId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa nhà cung cấp")
    public ResponseData<?> delete(@PathVariable String id) {
        log.info("API xóa nhà cung cấp với ID: {} được gọi", id);
        supplierService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Xóa nhà cung cấp thành công");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin nhà cung cấp theo ID")
    public ResponseData<SupplierResponse> findById(@PathVariable String id) {
        log.info("API lấy thông tin nhà cung cấp với ID: {} được gọi", id);
        SupplierResponse supplier = supplierService.findById(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy thông tin nhà cung cấp thành công", supplier);
    }

    // @GetMapping
    // @Operation(summary = "Lấy danh sách tất cả nhà cung cấp")
    // public ResponseData<List<SupplierResponse>> findAll() {
    //     log.info("API lấy danh sách tất cả nhà cung cấp được gọi");
    //     List<SupplierResponse> suppliers = supplierService.findAll();
    //     return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách nhà cung cấp thành công", suppliers);
    // }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm nhà cung cấp theo từ khóa")
    public ResponseData<List<SupplierResponse>> search(@RequestParam(required = false) String keyword) {
        log.info("API tìm kiếm nhà cung cấp với từ khóa: {} được gọi", keyword);
        List<SupplierResponse> suppliers = supplierService.search(keyword);
        return new ResponseData<>(HttpStatus.OK.value(), "Tìm kiếm nhà cung cấp thành công", suppliers);
    }
}
