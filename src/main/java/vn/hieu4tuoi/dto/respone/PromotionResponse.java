package vn.hieu4tuoi.dto.respone;

import lombok.Getter;
import lombok.Setter;
import vn.hieu4tuoi.dto.respone.product.ProductResponse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PromotionResponse {
    private String id;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer discount; //tính theo %
    private Long maximumDiscountAmount; //tính theo VNĐ
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<ProductResponse> products; // Danh sách sản phẩm áp dụng khuyến mãi
}
