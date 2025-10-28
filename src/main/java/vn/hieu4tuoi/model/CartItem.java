package vn.hieu4tuoi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cart_item")
public class CartItem extends AbstractEntity {

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "product_color_version_id")
    private String productColorVersionId;

    @Column(name = "user_id")
    private String userId;
}
