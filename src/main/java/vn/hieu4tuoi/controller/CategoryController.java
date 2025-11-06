package vn.hieu4tuoi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import vn.hieu4tuoi.dto.request.category.CategoryRequest;
import vn.hieu4tuoi.dto.respone.ResponseData;
import vn.hieu4tuoi.service.CategoryService;

@RestController
@RequestMapping("/category")
@Tag(name = "Category Controller")
@RequiredArgsConstructor
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseData<?> create(@RequestBody @Valid CategoryRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Tạo danh mục thành công", categoryService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseData<?> update(@PathVariable String id, @RequestBody @Valid CategoryRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Cập nhật danh mục thành công", categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> delete(@PathVariable String id) {
        categoryService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Xóa danh mục thành công");
    }

    @GetMapping("/{id}")
    public ResponseData<?> findById(@PathVariable String id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy thông tin danh mục thành công", categoryService.findById(id));
    }

    @GetMapping
    public ResponseData<?> findAll(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách danh mục thành công", categoryService.findAll(page, size));
    }

    @GetMapping("/public/all")
    public ResponseData<?> findAllWithoutPagination() {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách danh mục thành công", categoryService.findAllWithoutPagination());
    }
}

