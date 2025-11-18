package vn.hieu4tuoi.mapper;

import org.mapstruct.Mapper;
import vn.hieu4tuoi.dto.respone.product.ProductItemAdminResponse;
import vn.hieu4tuoi.model.ProductItem;

@Mapper(componentModel = "spring")
public interface ProductItemMapper {
    ProductItemAdminResponse entityToResponse(ProductItem productItem);
}