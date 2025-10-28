package vn.hieu4tuoi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "order_item")
public class OrderItem extends AbstractEntity {

    @Column(name = "selling_price", precision = 15, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "price_after_promotion", precision = 15, scale = 2)
    private BigDecimal priceAfterPromotion;

    @Column(name = "product_item_id")
    private String productItemId;

    @Column(name = "order_id")
    private String orderId;
}
