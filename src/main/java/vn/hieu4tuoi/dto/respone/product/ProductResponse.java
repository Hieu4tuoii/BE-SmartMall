package vn.hieu4tuoi.dto.respone.product;

import java.time.LocalDateTime;
import java.util.List;

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
    private String model;
    // private Integer warrantyPeriod;
    // private String description;
    // private String specifications;
    // private String status;
    // private String brandId;
    // private String categoryId;
    private Double averageRating;
    private Integer totalRating;
    private Long totalSold;
    private String imageUrl; //lấy 1 ảnh chính của sản phẩm
    private Long totalStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
