package vn.hieu4tuoi.mapper;

import org.mapstruct.Mapper;

import vn.hieu4tuoi.dto.request.category.BrandRequest;
import vn.hieu4tuoi.dto.respone.BrandResponse;
import vn.hieu4tuoi.model.Brand;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandResponse toResponse(Brand brand);
    Brand toEntity(BrandRequest request);
}

