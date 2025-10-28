package vn.hieu4tuoi.mapper;

import org.mapstruct.Mapper;

import vn.hieu4tuoi.dto.request.product.ProductCreateRequest;
import vn.hieu4tuoi.dto.respone.product.ProductForUpdateResponse;
import vn.hieu4tuoi.dto.respone.product.ProductResponse;
import vn.hieu4tuoi.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product createRequestToEntity(ProductCreateRequest request);
    ProductResponse entityToResponse(Product product);
    ProductForUpdateResponse entityToUpdateResponse(Product product);
}
