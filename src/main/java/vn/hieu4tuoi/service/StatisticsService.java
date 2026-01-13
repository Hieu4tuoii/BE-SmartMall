package vn.hieu4tuoi.service;

import vn.hieu4tuoi.dto.respone.statistics.DashboardStatisticsResponse;

public interface StatisticsService {

    /**
     * Lấy thống kê dashboard theo khoảng thời gian (tính theo createdAt của đơn).
     *
     * @param startDateTime thời gian bắt đầu (bao gồm)
     * @param endDateTime   thời gian kết thúc (bao gồm)
     * @return dữ liệu thống kê cho dashboard admin
     */
    DashboardStatisticsResponse getDashboardStatistics(java.time.LocalDateTime startDateTime,
                                                       java.time.LocalDateTime endDateTime);
}


