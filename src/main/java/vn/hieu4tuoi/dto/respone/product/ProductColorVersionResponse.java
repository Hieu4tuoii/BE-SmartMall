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
    private Long price;
    private String productVersionId;
    private String colorHex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
