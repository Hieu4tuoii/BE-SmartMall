package vn.hieu4tuoi.dto.request.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO để parse tham số đặt hàng từ AI Tool Order
 * Chứa thông tin sản phẩm và thông tin giao hàng từ chatbot
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderByAIRequest {
    private String productColorId;
    
    private String quantity;
    
    private String phoneNumber;
    
    private String address;
    
    private String note;
    
    private String paymentMethod;
}

