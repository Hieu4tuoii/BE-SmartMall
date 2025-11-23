package vn.hieu4tuoi.dto.respone.review;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO chứa danh sách đánh giá và thống kê đánh giá
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewListResponse {
    /**
     * Danh sách đánh giá có phân trang
     */
    private List<ReviewResponse> reviews;
    
    /**
     * Thống kê đánh giá (tổng số, trung bình, số lượng từng mức sao)
     */
    private ReviewStatistics statistics;
    
    /**
     * Số trang hiện tại
     */
    private int pageNo;
    
    /**
     * Kích thước trang
     */
    private int pageSize;
    
    /**
     * Tổng số trang
     */
    private int totalPage;
}

