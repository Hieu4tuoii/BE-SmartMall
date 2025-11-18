package vn.hieu4tuoi.dto.respone.review;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private String id;
    private String productVersionId;
    private String userFullName;
    private String comment;
    private Integer rating;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
