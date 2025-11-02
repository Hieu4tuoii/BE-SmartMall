package vn.hieu4tuoi.service;

import vn.hieu4tuoi.dto.request.importOrder.ImportOrderRequest;
import vn.hieu4tuoi.dto.request.importOrder.ProductImportSelectRequest;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.respone.importOder.ImportOrderDetailResponse;
import vn.hieu4tuoi.dto.respone.importOder.ImportOrderResponse;
import vn.hieu4tuoi.dto.respone.importOder.ProductImportSelectResponse;

import java.util.List;

public interface ImportOrderService {
    String create(ImportOrderRequest request);
    PageResponse<List<ImportOrderResponse>> getListImportOrders(int page, int size, String sort);
    List<ProductImportSelectResponse> getProductImportSelectList(ProductImportSelectRequest request);
    ImportOrderDetailResponse getById(String id);
}
