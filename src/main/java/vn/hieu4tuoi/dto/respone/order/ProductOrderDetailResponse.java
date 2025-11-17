package vn.hieu4tuoi.dto.respone.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductOrderDetailResponse {
    private String id;
    private String productName;
    private String productVersionName;
    private String colorName;
    private Long price;
    private String imeiOrSerial;
}
