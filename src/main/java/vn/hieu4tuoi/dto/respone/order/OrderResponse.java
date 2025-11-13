package vn.hieu4tuoi.dto.respone.order;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import vn.hieu4tuoi.common.OrderStatus;
import vn.hieu4tuoi.common.PaymentMethod;
import vn.hieu4tuoi.common.PaymentStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private String id;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private Long totalPrice;
    private CustomerOrderResponse customer;
    private List<ProductOrderResponse> products;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
