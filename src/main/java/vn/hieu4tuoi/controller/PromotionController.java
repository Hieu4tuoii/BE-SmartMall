package vn.hieu4tuoi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.hieu4tuoi.dto.request.promotion.PromotionRequest;
import vn.hieu4tuoi.dto.respone.ResponseData;
import vn.hieu4tuoi.service.PromotionService;

@RestController
@RequestMapping("/promotion")
@Tag(name = "Promotion Controller")
@RequiredArgsConstructor
@Validated
public class PromotionController {
    
    private final PromotionService promotionService;
    
    /**
     * Tạo mới chương trình giảm giá
     * @param request thông tin chương trình giảm giá
     * @return ID của chương trình giảm giá vừa tạo
     */
    @PostMapping
    public ResponseData<?> create(@RequestBody @Valid PromotionRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Tạo chương trình giảm giá thành công", promotionService.create(request));
    }
    
    /**
     * Cập nhật chương trình giảm giá
     * @param id ID của chương trình giảm giá
     * @param request thông tin cập nhật
     * @return ID của chương trình giảm giá đã cập nhật
     */
    @PutMapping("/{id}")
    public ResponseData<?> update(@PathVariable String id, @RequestBody @Valid PromotionRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Cập nhật chương trình giảm giá thành công", promotionService.update(id, request));
    }
    
    /**
     * Xóa chương trình giảm giá
     * @param id ID của chương trình giảm giá
     */
    @DeleteMapping("/{id}")
    public ResponseData<?> delete(@PathVariable String id) {
        promotionService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Xóa chương trình giảm giá thành công");
    }
    
    /**
     * Lấy thông tin chương trình giảm giá theo ID
     * @param id ID của chương trình giảm giá
     * @return thông tin chương trình giảm giá
     */
    @GetMapping("/{id}")
    public ResponseData<?> findById(@PathVariable String id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy thông tin chương trình giảm giá thành công", promotionService.findById(id));
    }
    
    /**
     * Lấy danh sách tất cả chương trình giảm giá
     * Sắp xếp theo thời gian tạo mới nhất lên đầu
     * @return danh sách chương trình giảm giá
     */
    @GetMapping
    public ResponseData<?> findAll() {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách chương trình giảm giá thành công", promotionService.findAll());
    }
}
