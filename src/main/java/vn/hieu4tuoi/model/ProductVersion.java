package vn.hieu4tuoi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_version")
public class ProductVersion extends AbstractEntity {

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "slug", length = 255)
    private String slug;
    //
    // @Column(name = "detailed_pecifications", columnDefinition = "TEXT")
    // private String detailedPecifications;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "promotion_id")
    private String promotionId;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "total_rating")
    private Integer totalRating;

    @Column(name = "total_sold")
    private Long totalSold;

    @Column(name = "price")
    private Long price;

    @Column(name = "full_text_search", length = 512)
    private String fullTextSearch;

    // @Column(name = "min_price")
    // private Long minPrice;

    // @Column(name = "max_discount")
    // private Integer maxDiscount;
    @PrePersist
    // khi tạo product version, tính toán total_sold, min_price, max_discount
    public void prePersist() {
        if (totalSold == null) {
            totalSold = 0L;
        }
        // if (minPrice == null) {
        //     minPrice = 0L;
        // }
    }
}
