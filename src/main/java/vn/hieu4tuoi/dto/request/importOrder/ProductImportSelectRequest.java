package vn.hieu4tuoi.dto.request.importOrder;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductImportSelectRequest {
    private String id;//product thì null
    @NotNull(message = "cấp độ không được để trống")
    private Integer level; //1: product, 2: product version, 3: product color version
}
