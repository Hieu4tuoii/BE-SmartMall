package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hieu4tuoi.model.ReturnRequest;
import java.util.List;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, String> {
    /**
     * Lấy danh sách yêu cầu trả hàng theo userId và isDeleted = false, sắp xếp theo ngày tạo giảm dần
     */
    List<ReturnRequest> findByUserIdAndIsDeletedOrderByCreatedAtDesc(String userId, Boolean isDeleted);
}
