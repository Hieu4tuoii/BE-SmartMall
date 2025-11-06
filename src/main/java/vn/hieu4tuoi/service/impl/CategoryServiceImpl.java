package vn.hieu4tuoi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.hieu4tuoi.dto.request.category.CategoryRequest;
import vn.hieu4tuoi.dto.respone.CategoryResponse;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.exception.ResourceNotFoundException;
import vn.hieu4tuoi.mapper.CategoryMapper;
import vn.hieu4tuoi.model.Category;
import vn.hieu4tuoi.repository.CategoryRepository;
import vn.hieu4tuoi.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public String create(CategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        category = categoryRepository.save(category);
        return category.getId();
    }

    @Override
    public String update(String id, CategoryRequest request) {
        Category category = categoryRepository.findByIdAndIsDeleted(id, false);
        if (category == null) {
            throw new ResourceNotFoundException("Không tìm thấy danh mục");
        }
        // category = categoryMapper.toEntity(request);
        category.setName(request.getName());
        category = categoryRepository.save(category);
        return category.getId();
    }

    @Override
    public void delete(String id) {
        Category category = categoryRepository.findByIdAndIsDeleted(id, false);
        if (category == null) {
            throw new ResourceNotFoundException("Không tìm thấy danh mục");
        }
        category.setIsDeleted(true);
        categoryRepository.save(category);
    }

    @Override
    public CategoryResponse findById(String id) {
        Category category = categoryRepository.findByIdAndIsDeleted(id, false);
        if (category == null) {
            throw new ResourceNotFoundException("Không tìm thấy danh mục");
        }
        return categoryMapper.toResponse(category);
    }

    @Override
    public PageResponse<List<CategoryResponse>> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryRepository.findAllByIsDeleted(false, pageable);
        List<CategoryResponse> responses = categoryPage.getContent()
                .stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<List<CategoryResponse>>builder()
                .pageNo(page)
                .pageSize(size)
                .totalPage(categoryPage.getTotalPages())
                .items(responses)
                .build();
    }

    @Override
    public List<CategoryResponse> findAllWithoutPagination() {
        List<Category> categories = categoryRepository.findAllByIsDeletedOrderByModifiedAtAsc(false);
        return categories.stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }
}

