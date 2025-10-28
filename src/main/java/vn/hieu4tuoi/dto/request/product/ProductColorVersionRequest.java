package vn.hieu4tuoi.dto.request.product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductColorVersionRequest {
    // private String id;
    private String color;
//    private String image;
    private String sku;
    private Long price;
    private String productVersionId;
    private String colorHex;
}
