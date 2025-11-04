package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import vn.hieu4tuoi.model.ProductVersion;

public interface ProductVersionRepository extends JpaRepository<ProductVersion, String> {
    ProductVersion findByIdAndIsDeleted(String id, Boolean isDeleted);
    
    List<ProductVersion> findByProductIdAndIsDeleted(String productId, Boolean isDeleted);
    
    /**
     * Tìm tất cả product versions theo danh sách ID và chưa bị xóa
     * @param ids Danh sách ID của product versions
     * @param isDeleted Trạng thái xóa
     * @return Danh sách product versions
     */
    List<ProductVersion> findByIdInAndIsDeleted(List<String> ids, Boolean isDeleted);

    /**
     * Tìm tất cả product versions có promotion_id cụ thể và chưa bị xóa
     * @param promotionId ID của promotion
     * @param isDeleted Trạng thái xóa
     * @return Danh sách product versions
     */
    List<ProductVersion> findByPromotionIdAndIsDeleted(String promotionId, Boolean isDeleted);
}
