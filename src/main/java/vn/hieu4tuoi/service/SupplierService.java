package vn.hieu4tuoi.service;

import vn.hieu4tuoi.dto.request.supplier.SupplierRequest;
import vn.hieu4tuoi.dto.respone.SupplierResponse;

import java.util.List;

public interface SupplierService {
    
    /**
     * Tạo mới nhà cung cấp
     * @param request thông tin nhà cung cấp
     * @return ID của nhà cung cấp vừa tạo
     */
    String create(SupplierRequest request);
    
    /**
     * Cập nhật thông tin nhà cung cấp
     * @param id ID của nhà cung cấp
     * @param request thông tin cập nhật
     * @return ID của nhà cung cấp đã cập nhật
     */
    String update(String id, SupplierRequest request);
    
    /**
     * Xóa nhà cung cấp (soft delete)
     * @param id ID của nhà cung cấp
     */
    void delete(String id);
    
    /**
     * Lấy thông tin nhà cung cấp theo ID
     * @param id ID của nhà cung cấp
     * @return thông tin nhà cung cấp
     */
    SupplierResponse findById(String id);
    
    /**
     * Lấy danh sách tất cả nhà cung cấp (không phân trang)
     * @return danh sách nhà cung cấp
     */
    // List<SupplierResponse> findAll();
    
    /**
     * Tìm kiếm nhà cung cấp theo từ khóa (không phân trang)
     * @param keyword từ khóa tìm kiếm
     * @return danh sách nhà cung cấp tìm được
     */
    List<SupplierResponse> search(String keyword);
}
