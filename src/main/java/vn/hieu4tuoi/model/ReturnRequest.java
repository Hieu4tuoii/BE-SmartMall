package vn.hieu4tuoi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.hieu4tuoi.common.ReturnRequestStatus;

@Getter
@Setter
@Entity
@Table(name = "return_request")
public class ReturnRequest extends AbstractEntity {

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "status", length = 50)
    private ReturnRequestStatus status = ReturnRequestStatus.PENDING; // Mặc định là đang chờ

    @Column(name = "order_item_id")
    private String orderItemId;
    
    @Column(name = "user_id")
    private String userId;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "address", length = 500)
    private String address;
}
