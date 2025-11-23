package vn.hieu4tuoi.dto.respone.order;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductOrderAdminResponse {
    private String orderItemId;
    private String productName;
    private String productVersionName;
    private String colorName;
    private Integer quantity;
}
