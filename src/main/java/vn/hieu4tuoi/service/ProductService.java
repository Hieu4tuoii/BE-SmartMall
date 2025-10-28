package vn.hieu4tuoi.service;

import java.util.List;

import vn.hieu4tuoi.dto.request.product.ProductColorVersionRequest;
import vn.hieu4tuoi.dto.request.product.ProductCreateRequest;
import vn.hieu4tuoi.dto.request.product.ProductVersionRequest;
import vn.hieu4tuoi.dto.request.product.ProductVersionUpdateRequest;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.respone.product.ProductForUpdateResponse;
import vn.hieu4tuoi.dto.respone.product.ProductResponse;
import vn.hieu4tuoi.dto.respone.product.ProductVersionResponse;

public interface ProductService {
    String create(ProductCreateRequest request);
    void update(String id, ProductCreateRequest request);
    void delete(String id);
    PageResponse<List<ProductResponse>> findAllInAdmin(int page, int size, String sort, String keyword);
    ProductForUpdateResponse findById(String id);
    String createVersion(ProductVersionRequest request);
    void updateVersion(String id, ProductVersionUpdateRequest request);
    void deleteVersion(String id);
    List<ProductVersionResponse> getVersionsByProductId(String productId);
    // ProductVersionResponse findVersionById(String id);
    String createColorVersion(ProductColorVersionRequest request);
    void updateColorVersion(String id, ProductColorVersionRequest request);
    void deleteColorVersion(String id);
}
