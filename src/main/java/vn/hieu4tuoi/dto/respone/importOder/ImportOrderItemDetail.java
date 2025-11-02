package vn.hieu4tuoi.dto.respone.importOder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImportOrderItemDetail {
    private String productId;
    private String productName;
    private String versionId;
    private String versionName;
    private String colorId;
    private String colorName;
    private BigDecimal importPrice;
    private Integer quantity;
    private List<String> imeiOrSerialList;
}


