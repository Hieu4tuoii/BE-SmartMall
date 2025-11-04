package vn.hieu4tuoi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import vn.hieu4tuoi.dto.request.banner.BannerRequest;
import vn.hieu4tuoi.dto.respone.ResponseData;
import vn.hieu4tuoi.service.BannerService;

@RestController
@RequestMapping("/banner")
@Tag(name = "Banner Controller")
@RequiredArgsConstructor
@Validated
public class BannerController {
    private final BannerService bannerService;

    @PostMapping
    public ResponseData<?> create(@RequestBody @Valid BannerRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Tạo banner thành công", bannerService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseData<?> update(@PathVariable String id, @RequestBody @Valid BannerRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Cập nhật banner thành công", bannerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> delete(@PathVariable String id) {
        bannerService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Xóa banner thành công");
    }

    @GetMapping("/public/all")
    public ResponseData<?> findAllWithoutPagination() {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách banner thành công", bannerService.findAllWithoutPagination());
    }
}


