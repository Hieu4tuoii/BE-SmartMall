package vn.hieu4tuoi.dto.respone.importOder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImportOrderDetailResponse {
    private String id;
    private String supplierId;
    private String supplierName;
    private Long totalImportPrice;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<ImportOrderItemDetail> items;
}


