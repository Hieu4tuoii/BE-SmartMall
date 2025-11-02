package vn.hieu4tuoi.dto.respone.importOder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImportOrderResponse {
    private String id;
    private String supplierName;
    private Long totalImportPrice;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
