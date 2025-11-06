package vn.hieu4tuoi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.hieu4tuoi.common.PaymentMethod;
import vn.hieu4tuoi.common.PaymentStatus;
import vn.hieu4tuoi.common.OrderStatus;

@Getter
@Setter
@Entity
@Table(name = "\"order\"")
public class Order extends AbstractEntity {

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "status", length = 50)
    private OrderStatus status = OrderStatus.PENDING; //mặc định là chờ xác nhận

    @Column(name = "payment_method", length = 50)
    private PaymentMethod paymentMethod = PaymentMethod.CASH; //mặc định là tiền mặt

    @Column(name = "payment_status", length = 50)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID; //mặc định là chưa thanh toán

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "user_id")
    private String userId;
}
