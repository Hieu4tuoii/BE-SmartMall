package vn.hieu4tuoi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.hieu4tuoi.dto.request.supplier.SupplierRequest;
import vn.hieu4tuoi.dto.respone.SupplierResponse;
import vn.hieu4tuoi.model.SupplierEntity;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierResponse toResponse(SupplierEntity supplierEntity);
    
    SupplierEntity toEntity(SupplierRequest request);
}
