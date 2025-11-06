package vn.hieu4tuoi.service;

import vn.hieu4tuoi.dto.request.order.OrderRequest;

public interface OrderService {
    String createOrder(OrderRequest request);
    
}
