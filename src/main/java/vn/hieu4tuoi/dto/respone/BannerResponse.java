package vn.hieu4tuoi.dto.respone;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BannerResponse {
    private String id;
    private String imageUrl;
    private String link;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}


