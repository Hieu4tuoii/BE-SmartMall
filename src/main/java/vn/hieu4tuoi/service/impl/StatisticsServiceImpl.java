package vn.hieu4tuoi.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.hieu4tuoi.common.OrderStatus;
import vn.hieu4tuoi.common.PaymentStatus;
import vn.hieu4tuoi.dto.respone.statistics.DashboardStatisticsResponse;
import vn.hieu4tuoi.dto.respone.statistics.DashboardStatisticsResponse.RevenueOrderPoint;
import vn.hieu4tuoi.dto.respone.statistics.DashboardStatisticsResponse.TopProductStatistic;
import vn.hieu4tuoi.model.ImportOrder;
import vn.hieu4tuoi.model.Order;
import vn.hieu4tuoi.model.OrderItem;
import vn.hieu4tuoi.model.Product;
import vn.hieu4tuoi.model.ProductColorVersion;
import vn.hieu4tuoi.model.ProductVersion;
import vn.hieu4tuoi.repository.ImportOrderRepository;
import vn.hieu4tuoi.repository.OrderItemRepository;
import vn.hieu4tuoi.repository.OrderRepository;
import vn.hieu4tuoi.repository.ProductColorVersionRepository;
import vn.hieu4tuoi.repository.ProductRepository;
import vn.hieu4tuoi.repository.ProductVersionRepository;
import vn.hieu4tuoi.service.StatisticsService;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ImportOrderRepository importOrderRepository;
    private final ProductRepository productRepository;
    private final ProductVersionRepository productVersionRepository;
    private final ProductColorVersionRepository productColorVersionRepository;

    @Override
    public DashboardStatisticsResponse getDashboardStatistics(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // Lấy các đơn hàng đã giao và đã thanh toán trong khoảng thời gian
        List<OrderStatus> completedStatuses = List.of(OrderStatus.DELIVERED);
        List<Order> orders = orderRepository.findAllByStatusInAndCreatedAtBetweenAndIsDeleted(
                completedStatuses,
                startDateTime,
                endDateTime,
                false
        ).stream()
                .filter(o -> o.getPaymentStatus() == PaymentStatus.PAID)
                .toList();

        // Lấy order items tương ứng
        List<String> orderIds = orders.stream().map(Order::getId).toList();
        List<OrderItem> orderItems = orderIds.isEmpty()
                ? List.of()
                // lấy cả các order item đã bị xóa để thống kê lịch sử doanh thu đầy đủ
                : orderItemRepository.findByOrderIdIn(orderIds);

        // Tính tổng doanh thu & tổng sản phẩm bán
        long totalRevenue = orderItems.stream()
                .mapToLong(oi -> oi.getDiscountedPrice() != null ? oi.getDiscountedPrice() : 0L)
                .sum();
        long totalProductsSold = orderItems.size();
        long totalOrders = orders.size();

        // Biểu đồ doanh thu & đơn hàng theo ngày
        Map<LocalDate, RevenueOrderPoint> chartMap = new HashMap<>();
        for (Order order : orders) {
            LocalDate date = order.getCreatedAt().toLocalDate();
            RevenueOrderPoint point = chartMap.getOrDefault(
                    date,
                    RevenueOrderPoint.builder()
                            .date(date)
                            .revenue(0L)
                            .orders(0L)
                            .build()
            );
            long orderRevenue = orderItems.stream()
                    .filter(oi -> oi.getOrderId().equals(order.getId()))
                    .mapToLong(oi -> oi.getDiscountedPrice() != null ? oi.getDiscountedPrice() : 0L)
                    .sum();
            point.setRevenue(point.getRevenue() + orderRevenue);
            point.setOrders(point.getOrders() + 1);
            chartMap.put(date, point);
        }

        List<RevenueOrderPoint> revenueOrderChart = chartMap.values().stream()
                .sorted(Comparator.comparing(RevenueOrderPoint::getDate))
                .toList();

        // Top sản phẩm bán chạy
        Map<String, Long> productVersionQuantityMap = new HashMap<>();
        Map<String, Long> productVersionRevenueMap = new HashMap<>();

        if (!orderItems.isEmpty()) {
            List<String> productColorVersionIds = orderItems.stream()
                    .map(OrderItem::getProductColorVersionId)
                    .distinct()
                    .toList();
            // lấy cả các bản ghi đã bị xóa để thống kê lịch sử đầy đủ
            List<ProductColorVersion> productColorVersions = productColorVersionRepository
                    .findAllByIdIn(productColorVersionIds);
            Map<String, ProductColorVersion> productColorVersionMap = productColorVersions.stream()
                    .collect(Collectors.toMap(ProductColorVersion::getId, pcv -> pcv));

            List<String> productVersionIds = productColorVersions.stream()
                    .map(ProductColorVersion::getProductVersionId)
                    .distinct()
                    .toList();
            List<ProductVersion> productVersions = productVersionRepository
                    .findAllByIdIn(productVersionIds);
            Map<String, ProductVersion> productVersionMap = productVersions.stream()
                    .collect(Collectors.toMap(ProductVersion::getId, pv -> pv));

            List<String> productIds = productVersions.stream()
                    .map(ProductVersion::getProductId)
                    .distinct()
                    .toList();
            List<Product> products = productRepository.findAllByIdIn(productIds);
            Map<String, Product> productMap = products.stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));

            for (OrderItem orderItem : orderItems) {
                ProductColorVersion pcv = productColorVersionMap.get(orderItem.getProductColorVersionId());
                if (pcv == null) {
                    continue;
                }
                ProductVersion pv = productVersionMap.get(pcv.getProductVersionId());
                if (pv == null) {
                    continue;
                }
                String productVersionId = pv.getId();
                long quantity = productVersionQuantityMap.getOrDefault(productVersionId, 0L) + 1;
                long revenue = productVersionRevenueMap.getOrDefault(productVersionId, 0L)
                        + (orderItem.getDiscountedPrice() != null ? orderItem.getDiscountedPrice() : 0L);
                productVersionQuantityMap.put(productVersionId, quantity);
                productVersionRevenueMap.put(productVersionId, revenue);
            }

            // Build danh sách top products
            List<TopProductStatistic> topProducts = productVersionQuantityMap.entrySet().stream()
                    .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                    .limit(5)
                    .map(entry -> {
                        String productVersionId = entry.getKey();
                        ProductVersion pv = productVersionMap.get(productVersionId);
                        Product product = pv != null ? productMap.get(pv.getProductId()) : null;
                        return TopProductStatistic.builder()
                                .productId(product != null ? product.getId() : null)
                                .productName(product != null ? product.getName() : null)
                                .productVersionId(productVersionId)
                                .productVersionName(pv != null ? pv.getName() : null)
                                .quantitySold(entry.getValue())
                                .revenue(productVersionRevenueMap.getOrDefault(productVersionId, 0L))
                                .build();
                    })
                    .toList();

            // Lấy tổng tiền nhập hàng trong khoảng thời gian
            List<ImportOrder> importOrders = importOrderRepository
                    .findAllByCreatedAtBetweenAndIsDeleted(startDateTime, endDateTime, false);
            long totalImportCost = importOrders.stream()
                    .mapToLong(io -> io.getTotalImportPrice() != null ? io.getTotalImportPrice() : 0L)
                    .sum();

            return DashboardStatisticsResponse.builder()
                    .totalRevenue(totalRevenue)
                    .totalImportCost(totalImportCost)
                    .totalOrders(totalOrders)
                    .totalProductsSold(totalProductsSold)
                    .revenueOrderChart(revenueOrderChart)
                    .topProducts(topProducts)
                    .build();
        }

        // Nếu không có order nào, vẫn cần tính tổng tiền nhập hàng
        List<ImportOrder> importOrders = importOrderRepository
                .findAllByCreatedAtBetweenAndIsDeleted(startDateTime, endDateTime, false);
        long totalImportCost = importOrders.stream()
                .mapToLong(io -> io.getTotalImportPrice() != null ? io.getTotalImportPrice() : 0L)
                .sum();

        return DashboardStatisticsResponse.builder()
                .totalRevenue(0L)
                .totalImportCost(totalImportCost)
                .totalOrders(0L)
                .totalProductsSold(0L)
                .revenueOrderChart(List.of())
                .topProducts(List.of())
                .build();
    }
}


