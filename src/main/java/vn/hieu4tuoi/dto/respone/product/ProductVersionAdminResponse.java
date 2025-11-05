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
public class ProductVersionAdminResponse {
    private String id;
    private String name;
    private String slug;
    private String productId;
    private Double averageRating;
    private Integer totalRating;
    private Long price;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<ProductColorVersionResponse> colorVersions;
}
