package vn.hieu4tuoi.service;

import vn.hieu4tuoi.dto.request.category.CategoryRequest;
import vn.hieu4tuoi.dto.respone.CategoryResponse;
import vn.hieu4tuoi.dto.respone.PageResponse;

import java.util.List;

public interface CategoryService {
    String create(CategoryRequest request);
    String update(String id, CategoryRequest request);
    void delete(String id);
    CategoryResponse findById(String id);
    PageResponse<List<CategoryResponse>> findAll(int page, int size);
    List<CategoryResponse> findAllWithoutPagination();
}

