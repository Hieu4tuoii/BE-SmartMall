package vn.hieu4tuoi.mapper;

import org.mapstruct.Mapper;

import vn.hieu4tuoi.dto.request.order.OrderRequest;
import vn.hieu4tuoi.dto.respone.order.OrderDetailResponse;
import vn.hieu4tuoi.dto.respone.order.OrderAdminResponse;
import vn.hieu4tuoi.model.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    
    Order requestToEntity(OrderRequest request);
    OrderAdminResponse entityToResponse(Order order);
    OrderDetailResponse entityToDetailResponse(Order order);
}
