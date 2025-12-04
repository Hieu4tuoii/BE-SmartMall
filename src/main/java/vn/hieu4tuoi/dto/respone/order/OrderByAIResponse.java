package vn.hieu4tuoi.dto.respone.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import vn.hieu4tuoi.common.PaymentMethod;

/**
 * DTO response cho kết quả đặt hàng từ AI
 * Chứa thông tin đơn hàng sau khi tạo thành công
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderByAIResponse {
    private String message;
    private String orderId;
    private Long totalPrice;
    private PaymentMethod paymentMethod;
}


