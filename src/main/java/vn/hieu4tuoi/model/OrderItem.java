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

    //giá sau khuyến mãi
    @Column(name = "discounted_price")
    private Long discountedPrice;

    @Column(name = "price")
    private Long price;//giá gốc

    @Column(name = "product_item_id")
    private String productItemId;

    @Column(name = "order_id")
    private String orderId;
    @Column(name = "product_color_version_id")
    private String productColorVersionId;
}
