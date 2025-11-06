package vn.hieu4tuoi.dto.request.cart;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCartItemRequest {
    @NotBlank(message = "ID sản phẩm không được để trống")
    private String productColorVersionId;
    @NotNull(message = "Số lượng không được để trống")
    private Integer quantity;
}
