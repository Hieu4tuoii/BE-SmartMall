package vn.hieu4tuoi.service;

import java.util.List;

import vn.hieu4tuoi.common.OrderStatus;
import vn.hieu4tuoi.dto.request.order.OrderByAIRequest;
import vn.hieu4tuoi.dto.request.order.OrderRequest;
import vn.hieu4tuoi.dto.request.order.UpdateOrderStatusRequest;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.respone.order.OrderAdminResponse;
import vn.hieu4tuoi.dto.respone.order.OrderDetailResponse;
import vn.hieu4tuoi.dto.respone.order.OrderResponse;

public interface OrderService {
    String createOrder(OrderRequest request);
    
    /**
     * Tạo đơn hàng thông qua AI Chatbot
     * @param request thông tin đặt hàng từ AI Tool
     * @return thông báo kết quả đặt hàng
     */
    String createOrderByAI(OrderByAIRequest request);
    PageResponse<List<OrderAdminResponse>> getOrderList(int page, int size, String sort, String keyword, OrderStatus status);

    List<OrderResponse> getOrderListByCurrentUser();

    OrderDetailResponse getOrderDetail(String id);

    void updateOrderStatus(String id, UpdateOrderStatusRequest request);
}
