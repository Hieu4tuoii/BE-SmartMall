package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hieu4tuoi.model.WarrantyClaim;
import java.util.List;

public interface WarrantyClaimRepository extends JpaRepository<WarrantyClaim, String> {
    /**
     * Lấy danh sách yêu cầu bảo hành theo userId và isDeleted = false, sắp xếp theo ngày tạo giảm dần
     */
    List<WarrantyClaim> findByUserIdAndIsDeletedOrderByCreatedAtDesc(String userId, Boolean isDeleted);
}
