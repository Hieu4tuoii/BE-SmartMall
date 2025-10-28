package vn.hieu4tuoi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "warranty_claim")
public class WarrantyClaim extends AbstractEntity {

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "product_item_id")
    private String productItemId;

    @Column(name = "user_id")
    private String userId;
}
