package vn.hieu4tuoi.service;

import vn.hieu4tuoi.dto.request.category.BrandRequest;
import vn.hieu4tuoi.dto.respone.BrandResponse;
import vn.hieu4tuoi.dto.respone.PageResponse;

import java.util.List;

public interface BrandService {
    String create(BrandRequest request);
    String update(String id, BrandRequest request);
    void delete(String id);
    BrandResponse findById(String id);
    PageResponse<List<BrandResponse>> findAll(int page, int size);
    List<BrandResponse> findAllWithoutPagination();
}

