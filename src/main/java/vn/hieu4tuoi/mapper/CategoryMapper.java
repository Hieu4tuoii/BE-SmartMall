package vn.hieu4tuoi.mapper;

import org.mapstruct.Mapper;

import vn.hieu4tuoi.dto.request.category.CategoryRequest;
import vn.hieu4tuoi.dto.respone.CategoryResponse;
import vn.hieu4tuoi.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toResponse(Category category);
    Category toEntity(CategoryRequest request);
}

