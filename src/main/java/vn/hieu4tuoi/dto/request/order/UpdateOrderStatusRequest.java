package vn.hieu4tuoi.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.hieu4tuoi.common.OrderStatus;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderStatusRequest {
    @NotNull(message = "Trạng thái đơn hàng không được để trống")
    private OrderStatus status;
    // @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    private List<ProductItemImeiRequest> productItemImeiList;
}
