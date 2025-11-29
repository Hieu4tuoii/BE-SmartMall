package vn.hieu4tuoi.service;

import java.util.List;

import vn.hieu4tuoi.dto.request.hybrid.HybridRagSearchRequest;
import vn.hieu4tuoi.dto.respone.hybrid.HybridRagSearchResponse;
import vn.hieu4tuoi.model.Product;
import vn.hieu4tuoi.model.ProductVersion;

public interface HybridRagService {
    void syncProductVersion(Product product, ProductVersion productVersion);

    /**
     * Tìm kiếm sản phẩm trong Hybrid RAG với các filter tùy chọn
     * 
     * @param request Request chứa query và các filter (minPrice, maxPrice, categoryId, brandId)
     * @return Danh sách kết quả tìm kiếm từ Hybrid RAG
     */
    List<HybridRagSearchResponse> searchProducts(HybridRagSearchRequest request);
}

