package vn.hieu4tuoi.dto.request.promotion;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PromotionRequest {
    
    @NotNull(message = "Thời gian bắt đầu không được để trống")
    private LocalDateTime startAt;
    
    @NotNull(message = "Thời gian kết thúc không được để trống")
    private LocalDateTime endAt;
    
    @NotNull(message = "Phần trăm giảm giá không được để trống")
    @Min(value = 1, message = "Phần trăm giảm giá phải lớn hơn 0")
    @Max(value = 100, message = "Phần trăm giảm giá không được vượt quá 100")
    private Long discount; //tính theo %
    
    private Long maximumDiscountAmount; //tính theo VNĐ
    
    private List<String> productIds; // Danh sách ID sản phẩm áp dụng khuyến mãi
}
