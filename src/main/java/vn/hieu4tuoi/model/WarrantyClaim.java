package vn.hieu4tuoi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.hieu4tuoi.common.WarrantyStatus;

@Getter
@Setter
@Entity
@Table(name = "warranty_claim")
public class WarrantyClaim extends AbstractEntity {

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "status", length = 50)
    private WarrantyStatus status = WarrantyStatus.PENDING; // Mặc định là đang chờ

    //orderitem id
    @Column(name = "order_item_id")
    private String orderItemId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "address", length = 500)
    private String address;
}
