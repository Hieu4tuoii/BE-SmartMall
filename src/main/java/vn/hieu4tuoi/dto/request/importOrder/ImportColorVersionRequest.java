package vn.hieu4tuoi.dto.request.importOrder;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImportColorVersionRequest {
   @NotBlank(message = "Màu sắc không được để trống")
   private String id;
   @NotNull(message = "Giá nhập không được để trống")
   private BigDecimal importPrice;
   private List<String> imeiOrSerialList;
}
