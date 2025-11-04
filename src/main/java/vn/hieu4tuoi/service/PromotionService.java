package vn.hieu4tuoi.service;

import vn.hieu4tuoi.dto.request.promotion.PromotionRequest;
import vn.hieu4tuoi.dto.respone.PromotionResponse;

import java.util.List;

public interface PromotionService {
    
    /**
     * Tạo mới chương trình giảm giá
     * @param request thông tin chương trình giảm giá
     * @return ID của chương trình giảm giá vừa tạo
     */
    String create(PromotionRequest request);
    
    /**
     * Cập nhật thông tin chương trình giảm giá
     * @param id ID của chương trình giảm giá
     * @param request thông tin cập nhật
     * @return ID của chương trình giảm giá đã cập nhật
     */
    String update(String id, PromotionRequest request);
    
    /**
     * Xóa chương trình giảm giá (soft delete)
     * @param id ID của chương trình giảm giá
     */
    void delete(String id);
    
    /**
     * Lấy thông tin chương trình giảm giá theo ID
     * @param id ID của chương trình giảm giá
     * @return thông tin chương trình giảm giá
     */
    PromotionResponse findById(String id);
    
    /**
     * Lấy danh sách tất cả chương trình giảm giá (không phân trang)
     * Sắp xếp theo thời gian tạo mới nhất lên đầu
     * @return danh sách chương trình giảm giá
     */
    List<PromotionResponse> findAll();
}
