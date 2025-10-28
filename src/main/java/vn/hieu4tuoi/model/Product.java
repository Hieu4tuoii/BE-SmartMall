package vn.hieu4tuoi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product")
public class Product extends AbstractEntity {

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "slug", length = 255)
    private String slug;

    @Column(name = "model", length = 255)
    private String model;

    @Column(name = "warranty_period")
    private Integer warrantyPeriod;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "specifications", columnDefinition = "TEXT")
    private String specifications;

    // @Column(name = "status", length = 50)
    // private String status;

    @Column(name = "brand_id")
    private String brandId;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "total_rating")
    private Integer totalRating;

    // tổng số lượng đã bán
    @Column(name = "total_sold")
    private Long totalSold;

    //tổng số hàng tồn kho
    @Column(name = "total_stock")
    private Long totalStock;
}
