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
public class ProductForUpdateResponse {
    private String id;
    private String name;
    private String model;
    private Integer warrantyPeriod;
    private String description;
    private String specifications;
    private String brandId;
    private String categoryId;
    private List<ImageResponse> imageList;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}

