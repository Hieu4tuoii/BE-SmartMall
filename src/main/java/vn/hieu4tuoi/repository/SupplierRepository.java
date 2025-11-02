package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hieu4tuoi.model.SupplierEntity;

import java.util.List;

public interface SupplierRepository extends JpaRepository<SupplierEntity, String> {
    
    /**
     * Tìm tất cả nhà cung cấp chưa bị xóa
     */
    List<SupplierEntity> findAllByIsDeleted(Boolean isDeleted);
    
    /**
     * Tìm nhà cung cấp theo ID và chưa bị xóa
     */
    SupplierEntity findByIdAndIsDeleted(String id, Boolean isDeleted);
    
    /**
     * Tìm kiếm nhà cung cấp theo từ khóa (tên, email, số điện thoại)
     */
    @Query("SELECT s FROM SupplierEntity s WHERE s.isDeleted = false AND " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "s.phoneNumber LIKE CONCAT('%', :keyword, '%'))")
    List<SupplierEntity> searchByKeyword(@Param("keyword") String keyword);

    List<SupplierEntity> findByIdInAndIsDeletedFalse(List<String> ids);
}
