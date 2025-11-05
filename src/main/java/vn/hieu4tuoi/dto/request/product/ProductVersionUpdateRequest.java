package vn.hieu4tuoi.dto.request.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductVersionUpdateRequest {
    // private String id;
    private String name;
    private String slug;
    private Long price;
//    private String detailedPecifications;
    // private List<ProductColorVersionRequest> productColorVersions;
    // private String productId;
}
