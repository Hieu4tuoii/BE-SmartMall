package vn.hieu4tuoi.dto.request.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageRequest {
    private String id;
    private String url;
    private Boolean isDefault;
    // private String productId;
}
