package vn.hieu4tuoi.dto.respone.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO chứa thống kê đánh giá cho một sản phẩm
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewStatistics {
    /**
     * Tổng số đánh giá
     */
    private Integer totalReviews;
    
    /**
     * Đánh giá trung bình (từ 1.0 đến 5.0)
     */
    private Double averageRating;
    
    /**
     * Số lượng đánh giá 1 sao
     */
    private Integer rating1Count;
    
    /**
     * Số lượng đánh giá 2 sao
     */
    private Integer rating2Count;
    
    /**
     * Số lượng đánh giá 3 sao
     */
    private Integer rating3Count;
    
    /**
     * Số lượng đánh giá 4 sao
     */
    private Integer rating4Count;
    
    /**
     * Số lượng đánh giá 5 sao
     */
    private Integer rating5Count;
}

