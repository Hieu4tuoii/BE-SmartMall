package vn.hieu4tuoi.mapper;

import org.mapstruct.Mapper;

import vn.hieu4tuoi.dto.request.product.ProductVersionRequest;
import vn.hieu4tuoi.dto.respone.product.ProductVersionAdminResponse;
import vn.hieu4tuoi.dto.respone.product.ProductVersionDetailResponse;
import vn.hieu4tuoi.dto.respone.product.ProductVersionResponse;
import vn.hieu4tuoi.model.ProductVersion;

@Mapper(componentModel = "spring")
public interface ProductVersionMapper {
    ProductVersion requestToEntity(ProductVersionRequest request);
    ProductVersionAdminResponse entityToResponse(ProductVersion productVersion);
    ProductVersionResponse entityToPublicResponse(ProductVersion productVersion);
    ProductVersionDetailResponse entityToDetailResponse(ProductVersion productVersion);
}
