package vn.hieu4tuoi.mapper;

import org.mapstruct.Mapper;

import vn.hieu4tuoi.dto.request.product.ProductVersionRequest;
import vn.hieu4tuoi.dto.respone.product.ProductVersionResponse;
import vn.hieu4tuoi.model.ProductVersion;

@Mapper(componentModel = "spring")
public interface ProductVersionMapper {
    ProductVersion requestToEntity(ProductVersionRequest request);
    ProductVersionResponse entityToResponse(ProductVersion productVersion);
}
