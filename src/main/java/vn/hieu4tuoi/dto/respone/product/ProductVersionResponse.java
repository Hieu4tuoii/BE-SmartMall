package vn.hieu4tuoi.dto.respone.product;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductVersionResponse {
    private String id;
    private String name;
    private String productId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProductColorVersionResponse> colorVersions;
}
