package vn.hieu4tuoi.dto.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreationRequest {
    private String productVersionId;
    private String comment;
    //bắt buộc phải là 1 hoặc 2 hoặc 3 hoặc 4 hoặc 5
    @NotNull(message = "Đánh giá không được để trống")
    @Min(value = 1, message = "Đánh giá phải lớn hơn 0")
    @Max(value = 5, message = "Đánh giá phải nhỏ hơn 5")
    private Integer rating;
}
