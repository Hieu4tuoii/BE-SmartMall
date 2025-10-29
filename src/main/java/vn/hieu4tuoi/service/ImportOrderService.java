package vn.hieu4tuoi.service;

import vn.hieu4tuoi.dto.request.importOrder.ImportOrderRequest;

public interface ImportOrderService {
    String create(ImportOrderRequest request);
}
