package vn.hieu4tuoi.dto.request.product;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateRequest {
    private String name;
    private String model;
    private Integer warrantyPeriod;
    private String description;
    private String specifications;
//    private String status;
    private String brandId;
    private String categoryId;
    // private List<ProductVersionRequest> productVersions;
    private List<ImageRequest> imageList;
}
