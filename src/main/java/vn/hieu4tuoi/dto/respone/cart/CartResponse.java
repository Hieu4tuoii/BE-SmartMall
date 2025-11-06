package vn.hieu4tuoi.dto.respone.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private Integer totalItem;
    private Long totalPrice;
    private List<CartItemResponse> cartItems;
}
