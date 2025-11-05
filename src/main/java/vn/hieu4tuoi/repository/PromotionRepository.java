package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hieu4tuoi.model.Promotion;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, String> {
    
    /**
     * Tìm promotion theo ID và isDeleted
     * @param id ID của promotion
     * @param isDeleted trạng thái xóa
     * @return Promotion nếu tìm thấy
     */
    Promotion findByIdAndIsDeleted(String id, Boolean isDeleted);
    
    /**
     * Lấy danh sách tất cả promotion chưa bị xóa, sắp xếp theo thời gian tạo mới nhất
     * @param isDeleted trạng thái xóa
     * @return Danh sách promotion
     */
    @Query("SELECT p FROM Promotion p WHERE p.isDeleted = :isDeleted ORDER BY p.createdAt DESC")
    List<Promotion> findAllByIsDeletedOrderByCreatedAtDesc(@Param("isDeleted") Boolean isDeleted);

    //lấy ds khuyến mãi theo id in và thời gian hiện tại và chưa bị xóa
    @Query("SELECT p FROM Promotion p WHERE p.id IN :ids AND p.startAt <= :now AND p.endAt >= :now AND p.isDeleted = :isDeleted")
    List<Promotion> findAllByIdInAndStartAtLessThanEqualAndEndAtGreaterThanEqual(@Param("ids") List<String> ids, @Param("now") LocalDateTime now, @Param("isDeleted") Boolean isDeleted);
}
