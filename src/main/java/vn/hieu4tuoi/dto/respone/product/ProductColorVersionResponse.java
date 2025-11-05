package vn.hieu4tuoi.dto.respone.product;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductColorVersionResponse {
    private String id;
    private String color;
    private String sku;
    private String productVersionId;
    private Long totalStock;
    private Long totalSold;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
