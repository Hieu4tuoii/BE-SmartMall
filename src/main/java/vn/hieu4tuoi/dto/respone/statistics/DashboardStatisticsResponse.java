package vn.hieu4tuoi.dto.respone.statistics;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsResponse {
    /**
     * Tổng doanh thu (đơn đã giao và đã thanh toán) trong khoảng thời gian.
     */
    private Long totalRevenue;

    /**
     * Tổng tiền nhập hàng trong khoảng thời gian.
     */
    private Long totalImportCost;

    /**
     * Tổng số đơn hàng được tạo trong khoảng thời gian.
     */
    private Long totalOrders;

    /**
     * Tổng số lượng sản phẩm đã bán ra trong khoảng thời gian.
     */
    private Long totalProductsSold;

    /**
     * Dữ liệu biểu đồ doanh thu & đơn hàng theo ngày.
     */
    private List<RevenueOrderPoint> revenueOrderChart;

    /**
     * Danh sách top sản phẩm bán chạy.
     */
    private List<TopProductStatistic> topProducts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueOrderPoint {
        private LocalDate date;
        private Long revenue;
        private Long orders;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProductStatistic {
        private String productId;
        private String productName;
        private String productVersionId;
        private String productVersionName;
        private Long quantitySold;
        private Long revenue;
    }
}


