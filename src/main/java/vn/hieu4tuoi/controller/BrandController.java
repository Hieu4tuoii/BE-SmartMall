package vn.hieu4tuoi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import vn.hieu4tuoi.dto.request.category.BrandRequest;
import vn.hieu4tuoi.dto.respone.ResponseData;
import vn.hieu4tuoi.service.BrandService;

@RestController
@RequestMapping("/brand")
@Tag(name = "Brand Controller")
@RequiredArgsConstructor
@Validated
public class BrandController {
    private final BrandService brandService;

    @PostMapping
    public ResponseData<?> create(@RequestBody @Valid BrandRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Tạo thương hiệu thành công", brandService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseData<?> update(@PathVariable String id, @RequestBody @Valid BrandRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Cập nhật thương hiệu thành công", brandService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> delete(@PathVariable String id) {
        brandService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Xóa thương hiệu thành công");
    }

    @GetMapping("/{id}")
    public ResponseData<?> findById(@PathVariable String id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy thông tin thương hiệu thành công", brandService.findById(id));
    }

    @GetMapping
    public ResponseData<?> findAll(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách thương hiệu thành công", brandService.findAll(page, size));
    }

    @GetMapping("/all")
    public ResponseData<?> findAllWithoutPagination() {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách thương hiệu thành công", brandService.findAllWithoutPagination());
    }
}

