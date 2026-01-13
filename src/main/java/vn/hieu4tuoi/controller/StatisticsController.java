package vn.hieu4tuoi.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import vn.hieu4tuoi.dto.respone.ResponseData;
import vn.hieu4tuoi.dto.respone.statistics.DashboardStatisticsResponse;
import vn.hieu4tuoi.service.StatisticsService;

@RestController
@RequestMapping("/statistics")
@Tag(name = "Statistics Controller")
@RequiredArgsConstructor
@Validated
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Lấy thống kê dashboard theo khoảng thời gian.
     *
     * @param range khoảng thời gian: today, last7days, last30days
     */
    @GetMapping("/dashboard")
    public ResponseData<DashboardStatisticsResponse> getDashboardStatistics(
            @RequestParam(defaultValue = "today") String range) {
        LocalDate today = LocalDate.now();
        LocalDate startDate;

        switch (range) {
            case "last7days":
                startDate = today.minusDays(6);
                break;
            case "last30days":
                startDate = today.minusDays(29);
                break;
            case "today":
            default:
                startDate = today;
                break;
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = today.atTime(23, 59, 59);

        DashboardStatisticsResponse data = statisticsService.getDashboardStatistics(startDateTime, endDateTime);
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy thống kê dashboard thành công", data);
    }
}


