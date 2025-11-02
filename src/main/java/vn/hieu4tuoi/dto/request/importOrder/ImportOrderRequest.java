package vn.hieu4tuoi.dto.request.importOrder;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImportOrderRequest {
    @NotBlank(message = "Nhà cung cấp không được để trống")
    private String supplierId;
    @NotEmpty(message = "Danh sách màu sắc không được để trống")
    private List<ImportColorVersionRequest> importColorVersionList;
}
