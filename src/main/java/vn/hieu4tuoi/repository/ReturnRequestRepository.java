package vn.hieu4tuoi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hieu4tuoi.common.ReturnRequestStatus;
import vn.hieu4tuoi.model.ReturnRequest;
import java.util.List;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, String> {
    /**
     * Lấy danh sách yêu cầu trả hàng theo userId và isDeleted = false, sắp xếp theo ngày tạo giảm dần
     */
    List<ReturnRequest> findByUserIdAndIsDeletedOrderByCreatedAtDesc(String userId, Boolean isDeleted);
    
    /**
     * Lấy danh sách yêu cầu trả hàng cho admin với phân trang và filter theo status
     */
    @Query("SELECT r FROM ReturnRequest r WHERE r.isDeleted = false AND (:status is null or r.status = :status) ORDER BY r.createdAt DESC")
    Page<ReturnRequest> findAllByStatusAndIsDeleted(@Param("status") ReturnRequestStatus status, Pageable pageable);
    
    /**
     * Lấy yêu cầu trả hàng theo id và isDeleted = false
     */
    ReturnRequest findByIdAndIsDeleted(String id, Boolean isDeleted);
}
