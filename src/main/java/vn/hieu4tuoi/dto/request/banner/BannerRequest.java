package vn.hieu4tuoi.dto.request.banner;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerRequest {
    @NotBlank(message = "imageUrl không được để trống")
    private String imageUrl;

    private String link;
}


