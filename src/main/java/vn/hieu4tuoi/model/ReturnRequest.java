package vn.hieu4tuoi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "return_request")
public class ReturnRequest extends AbstractEntity {

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "order_item_id")
    private String orderItemId;
}
