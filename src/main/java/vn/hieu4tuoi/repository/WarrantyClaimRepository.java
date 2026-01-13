package vn.hieu4tuoi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hieu4tuoi.common.WarrantyStatus;
import vn.hieu4tuoi.model.WarrantyClaim;
import java.util.List;

public interface WarrantyClaimRepository extends JpaRepository<WarrantyClaim, String> {
    /**
     * Lấy danh sách yêu cầu bảo hành theo userId và isDeleted = false, sắp xếp theo ngày tạo giảm dần
     */
    List<WarrantyClaim> findByUserIdAndIsDeletedOrderByCreatedAtDesc(String userId, Boolean isDeleted);
    
    /**
     * Lấy danh sách yêu cầu bảo hành cho admin với phân trang và filter theo status
     */
    @Query("SELECT w FROM WarrantyClaim w WHERE w.isDeleted = false AND (:status is null or w.status = :status) ORDER BY w.createdAt DESC")
    Page<WarrantyClaim> findAllByStatusAndIsDeleted(@Param("status") WarrantyStatus status, Pageable pageable);
    
    /**
     * Lấy yêu cầu bảo hành theo id và isDeleted = false
     */
    WarrantyClaim findByIdAndIsDeleted(String id, Boolean isDeleted);
}
