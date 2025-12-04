package vn.hieu4tuoi.dto.respone.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Thông tin sản phẩm trong yêu cầu trả hàng (không bao gồm giá)
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductReturnResponse {
    private String orderItemId;
    private String productName;
    private String productVersionName;
    private String colorName;
    private String imeiOrSerial;
    private String imageUrl;
}

