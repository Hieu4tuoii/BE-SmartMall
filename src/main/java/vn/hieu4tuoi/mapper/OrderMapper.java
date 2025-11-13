package vn.hieu4tuoi.mapper;

import org.mapstruct.Mapper;

import vn.hieu4tuoi.dto.request.order.OrderRequest;
import vn.hieu4tuoi.dto.respone.order.OrderResponse;
import vn.hieu4tuoi.model.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    
    Order requestToEntity(OrderRequest request);
    OrderResponse entityToResponse(Order order);
}
