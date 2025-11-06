package vn.hieu4tuoi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.hieu4tuoi.dto.request.product.ProductColorVersionRequest;
import vn.hieu4tuoi.dto.request.product.ProductCreateRequest;
import vn.hieu4tuoi.dto.request.product.ProductVersionRequest;
import vn.hieu4tuoi.dto.request.product.ProductVersionUpdateRequest;
import vn.hieu4tuoi.dto.respone.ResponseData;
import vn.hieu4tuoi.service.ProductService;

@RestController
@RequestMapping("/product")
@Tag(name = "Product Controller")
@RequiredArgsConstructor
@Validated
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseData<String> create(@RequestBody @Valid ProductCreateRequest request) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Tạo sản phẩm thành công", productService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseData<?> update(@PathVariable String id, @RequestBody @Valid ProductCreateRequest request) {
        productService.update(id, request);
        return new ResponseData<>(HttpStatus.OK.value(), "Cập nhật sản phẩm thành công");
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> delete(@PathVariable String id) {
        productService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Xóa sản phẩm thành công");
    }

    @GetMapping("/list")
    public ResponseData<?> findAll(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size,
                                   @RequestParam(defaultValue = "id") String sort,
                                   @RequestParam(defaultValue = "") String keyword) {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách sản phẩm thành công", productService.findAllInAdmin(page, size, sort, keyword));
    }

    @GetMapping("/{id}")
    public ResponseData<?> findById(@PathVariable String id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy sản phẩm thành công", productService.findById(id));
    }

    @GetMapping("/{id}/versions")
    public ResponseData<?> getVersionsByProductId(@PathVariable String id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách phiên bản thành công", productService.getVersionsByProductId(id));
    }

    @GetMapping("/versions/all")
    public ResponseData<?> getAllVersions() {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy tất cả phiên bản sản phẩm thành công", productService.getAllVersions());
    }

    @GetMapping("/public/version/search")
    public ResponseData<?> searchPublicVersion(@RequestParam(required = false) Boolean hasPromotion, @RequestParam(required = false) List<String> brandIds, @RequestParam(required = false) List<String> categoryIds, @RequestParam(required = false) Long minPrice, @RequestParam(required = false) Long maxPrice, @RequestParam(defaultValue = "") String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String sort) {
        return new ResponseData<>(HttpStatus.OK.value(), "Tim kiem sản phẩm thành công", productService.searchPublicProductVersion(brandIds, categoryIds, hasPromotion, minPrice, maxPrice, keyword, page, size, sort));
    }

    @GetMapping("/public/version/{slug}")
    public ResponseData<?> findVersionDetailBySlug(@PathVariable String slug) {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy chi tiết phiên bản sản phẩm thành công", productService.findVersionDetailBySlug(slug));
    }

    @PostMapping("/version")
    public ResponseData<String> createVersion(@RequestBody @Valid ProductVersionRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Tạo phiên bản sản phẩm thành công", productService.createVersion(request));
    }
    
    @PutMapping("/version/{id}")
    public ResponseData<?> updateVersion(@PathVariable String id, @RequestBody @Valid ProductVersionUpdateRequest request) {
        productService.updateVersion(id, request);
        return new ResponseData<>(HttpStatus.OK.value(), "Cập nhật phiên bản sản phẩm thành công");
    }
    
    @DeleteMapping("/version/{id}")
    public ResponseData<?> deleteVersion(@PathVariable String id) {
        productService.deleteVersion(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Xóa phiên bản sản phẩm thành công");
    }

    @PostMapping("/color-version")
    public ResponseData<String> createColorVersion(@RequestBody @Valid ProductColorVersionRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Tạo phiên bản sản phẩm thành công", productService.createColorVersion(request));
    }
    
    @PutMapping("/color-version/{id}")
    public ResponseData<?> updateColorVersion(@PathVariable String id, @RequestBody @Valid ProductColorVersionRequest request) {
        productService.updateColorVersion(id, request);
        return new ResponseData<>(HttpStatus.OK.value(), "Cập nhật phiên bản sản phẩm thành công");
    }

    @DeleteMapping("/color-version/{id}")
    public ResponseData<?> deleteColorVersion(@PathVariable String id) {
        productService.deleteColorVersion(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Xóa phiên bản sản phẩm thành công");
    }
}
