package vn.hieu4tuoi.dto.respone.cart;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    private String id;
    private String productColorVersionId;
    private String productName;
    private String productVersionName;
    private String colorName;
    private String slug;
    private String imageUrl;
    private Integer quantity;
    private Long totalPrice;//giá này là giá đã sau khuyến mãi
}
