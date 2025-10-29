package vn.hieu4tuoi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.hieu4tuoi.dto.request.supplier.SupplierRequest;
import vn.hieu4tuoi.dto.respone.SupplierResponse;
import vn.hieu4tuoi.exception.ResourceNotFoundException;
import vn.hieu4tuoi.mapper.SupplierMapper;
import vn.hieu4tuoi.model.SupplierEntity;
import vn.hieu4tuoi.repository.SupplierRepository;
import vn.hieu4tuoi.service.SupplierService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "SUPPLIER-SERVICE")
public class SupplierServiceImpl implements SupplierService {
    
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    @Transactional
    public String create(SupplierRequest request) {
        log.info("Tạo mới nhà cung cấp với tên: {}", request.getName());
        
        SupplierEntity supplier = supplierMapper.toEntity(request);
        supplier = supplierRepository.save(supplier);
        
        log.info("Tạo nhà cung cấp thành công với ID: {}", supplier.getId());
        return supplier.getId();
    }

    @Override
    @Transactional
    public String update(String id, SupplierRequest request) {
        log.info("Cập nhật nhà cung cấp với ID: {}", id);
        
        SupplierEntity supplier = supplierRepository.findByIdAndIsDeleted(id, false);
        if (supplier == null) {
            throw new ResourceNotFoundException("Không tìm thấy nhà cung cấp ");
        }
        
        // Cập nhật thông tin từ request
        supplier.setName(request.getName());
        supplier.setEmail(request.getEmail());
        supplier.setPhoneNumber(request.getPhoneNumber());
        
        supplier = supplierRepository.save(supplier);
        
        log.info("Cập nhật nhà cung cấp thành công với ID: {}", supplier.getId());
        return supplier.getId();
    }

    @Override
    @Transactional
    public void delete(String id) {
        log.info("Xóa nhà cung cấp với ID: {}", id);
        
        SupplierEntity supplier = supplierRepository.findByIdAndIsDeleted(id, false);
        if (supplier == null) {
            throw new ResourceNotFoundException("Không tìm thấy nhà cung cấp ");
        }
        
        supplier.setIsDeleted(true);
        supplierRepository.save(supplier);
        
        log.info("Xóa nhà cung cấp thành công với ID: {}", id);
    }

    @Override
    public SupplierResponse findById(String id) {
        log.info("Lấy thông tin nhà cung cấp với ID: {}", id);
        
        SupplierEntity supplier = supplierRepository.findByIdAndIsDeleted(id, false);
        if (supplier == null) {
            throw new ResourceNotFoundException("Không tìm thấy nhà cung cấp ");
        }
        
        return supplierMapper.toResponse(supplier);
    }

    // @Override
    // public List<SupplierResponse> findAll() {
    //     log.info("Lấy danh sách tất cả nhà cung cấp");
        
    //     List<SupplierEntity> suppliers = supplierRepository.findAllByIsDeleted(false);
    //     return suppliers.stream()
    //             .map(supplierMapper::toResponse)
    //             .collect(Collectors.toList());
    // }

    @Override
    public List<SupplierResponse> search(String keyword) {
        log.info("Tìm kiếm nhà cung cấp với từ khóa: {}", keyword);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            keyword = "";
        } 
        return supplierRepository.searchByKeyword(keyword.trim()).stream()
                .map(supplierMapper::toResponse)
                .collect(Collectors.toList());
    }
}
