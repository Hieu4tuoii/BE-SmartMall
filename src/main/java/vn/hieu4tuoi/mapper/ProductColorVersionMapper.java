package vn.hieu4tuoi.mapper;

import org.mapstruct.Mapper;

import vn.hieu4tuoi.dto.request.product.ProductColorVersionRequest;
import vn.hieu4tuoi.dto.respone.product.ProductColorVersionResponse;
import vn.hieu4tuoi.model.ProductColorVersion;

@Mapper(componentModel = "spring")
public interface ProductColorVersionMapper {
    ProductColorVersion requestToEntity(ProductColorVersionRequest request);
    ProductColorVersionResponse entityToResponse(ProductColorVersion productColorVersion);
}
