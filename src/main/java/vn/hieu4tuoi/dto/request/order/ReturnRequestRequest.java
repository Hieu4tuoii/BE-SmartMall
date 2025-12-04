package vn.hieu4tuoi.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.hieu4tuoi.common.ReturnRequestType;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReturnRequestRequest {
    private String reason;
    @NotBlank(message = "ID đơn hàng không được để trống")
    private String orderItemId;
    private ReturnRequestType returnRequestType;
    
    private String phoneNumber;
    
    private String address;
}
