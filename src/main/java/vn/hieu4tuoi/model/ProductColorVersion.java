package vn.hieu4tuoi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_color_version")
public class ProductColorVersion extends AbstractEntity {

    @Column(name = "color", length = 100)
    private String color;
//
//    @Column(name = "image", length = 500)
//    private String image;

    @Column(name = "sku", length = 100)
    private String sku;

    @Column(name = "price", precision = 15, scale = 2)
    private Long price;

    @Column(name = "product_version_id")
    private String productVersionId;

    // Mã màu đã bỏ khỏi hệ thống
    //tổng số hàng tồn kho
    @Column(name = "total_stock")
    private Long totalStock;

    @PrePersist
    public void prePersist() {
        if (totalStock == null) {
            totalStock = 0L;
        }
    }
}
