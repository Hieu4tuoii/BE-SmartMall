package vn.hieu4tuoi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "request_for_exchange")
public class RequestForExchange extends AbstractEntity {

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "order_item_id")
    private String orderItemId;

    @Column(name = "new_product_item_id")
    private String newProductItemId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "address", length = 500)
    private String address;
}
