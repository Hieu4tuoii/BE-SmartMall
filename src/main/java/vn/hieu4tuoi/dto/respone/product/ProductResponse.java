package vn.hieu4tuoi.dto.respone.product;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private String id;
    private String name;
    private String slug;
    // private String imageUrl;
    private Double averageRating;
    private Integer totalRating;
    //tổng số lượng đã bán
    private Long totalSold;
    //giá gốc
    private Long price;
    //giá khuyến mãi
    private Long discount; //tính theo %
    //giá sau khuyến mãi
    private Long discountedPrice;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
