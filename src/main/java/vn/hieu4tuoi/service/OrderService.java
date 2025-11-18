package vn.hieu4tuoi.service;

import java.util.List;

import vn.hieu4tuoi.common.OrderStatus;
import vn.hieu4tuoi.dto.request.order.OrderRequest;
import vn.hieu4tuoi.dto.request.order.UpdateOrderStatusRequest;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.respone.order.OrderResponse;
import vn.hieu4tuoi.dto.respone.order.OrderDetailResponse;

public interface OrderService {
    String createOrder(OrderRequest request);
    PageResponse<List<OrderResponse>> getOrderList(int page, int size, String sort, String keyword, OrderStatus status);

    OrderDetailResponse getOrderDetail(String id);

    void updateOrderStatus(String id, UpdateOrderStatusRequest request);
}
