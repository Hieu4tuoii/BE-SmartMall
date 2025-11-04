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
//
//    @Column(name = "detailed_pecifications", columnDefinition = "TEXT")
//    private String detailedPecifications;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "promotion_id")
    private String promotionId;
}
