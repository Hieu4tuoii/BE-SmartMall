package vn.hieu4tuoi.dto.request.importOrder;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImportColorVersionRequest {
   private String id;
   private BigDecimal importPrice;
   private List<String> imeiOrSerialList;
}
