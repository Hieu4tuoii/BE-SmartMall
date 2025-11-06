package vn.hieu4tuoi.dto.respone.product;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hieu4tuoi.dto.respone.BrandResponse;
import vn.hieu4tuoi.dto.respone.CategoryResponse;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductVersionDetailResponse {
    private String id;
    private String name;
    private String slug;
    private List<String> imageUrls;
    private Double averageRating;
    private Integer totalRating;
    // tổng số lượng đã bán
    private Long totalSold;
    // giá gốc
    private Long price;
    // giá khuyến mãi
    private Long discount; // tính theo %
    // giá sau khuyến mãi
    private Long discountedPrice;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private BrandResponse brand;
    private CategoryResponse category;
    private String specifications;
    private String model;
    private Integer warrantyPeriod;
    private String description;
    private List<ProductVersionNameResponse> productVersionNames;
    private List<ProductColorVersionResponse> productColorVersions;
}
