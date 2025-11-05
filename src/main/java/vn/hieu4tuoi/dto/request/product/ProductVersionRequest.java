package vn.hieu4tuoi.dto.request.product;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductVersionRequest {
    // private String id;
    private String name;
    private String slug;
//    private String detailedPecifications;
    private String productId;
    private Long price;
    // private List<ProductColorVersionRequest> productColorVersions;
    // private String productId;
}
