package vn.hieu4tuoi.dto.respone;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BrandResponse {
    private String id;
    private String name;
//    private String slug;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}

