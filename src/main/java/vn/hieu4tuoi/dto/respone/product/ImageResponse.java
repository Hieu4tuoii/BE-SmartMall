package vn.hieu4tuoi.dto.respone.product;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse {
    private String id;
    private String url;
    private Boolean isDefault;
}
