package vn.hieu4tuoi.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductItemImeiRequest {
    @NotBlank(message = "IMEI hoặc serial không được để trống")
    private String imeiOrSerial;
    @NotBlank(message = "ID đơn hàng không được để trống")
    private String orderItemId;
}
