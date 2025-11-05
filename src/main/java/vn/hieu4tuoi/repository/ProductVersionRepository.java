package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.time.LocalDateTime;
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


    //get ds phiên bản có sắp xếp theo pri

    /**
     * Lấy danh sách ProductVersion phân trang theo danh sách brand hoặc category (có thể truyền null).
     */
    // @Query(
    //     value = """
    //         SELECT pv
    //         FROM ProductVersion pv
    //         JOIN Product p ON p.id = pv.productId
    //         WHERE pv.isDeleted = false
    //           AND p.isDeleted = false
    //           AND (
    //                 (:brandIds IS NULL OR p.brandId IN :brandIds)
    //               AND (:categoryIds IS NULL OR p.categoryId IN :categoryIds)
    //           )
    //           AND pv.fullTextSearch LIKE :keyword
    //     """
    //         //   ,
    //     // countQuery = """
    //     //     SELECT COUNT(pv)
    //     //     FROM ProductVersion pv
    //     //     JOIN Product p ON p.id = pv.productId
    //     //     WHERE pv.isDeleted = false
    //     //       AND p.isDeleted = false
    //     //       AND (
    //     //             (:brandIds IS NULL OR p.brandId IN :brandIds)
    //     //          OR (:categoryIds IS NULL OR p.categoryId IN :categoryIds)
    //     //       )
    //     // """
    // )
    // Page<ProductVersion> findPageByBrandIdsOrCategoryIds(
    //     @Param("brandIds") List<String> brandIds,
    //     @Param("categoryIds") List<String> categoryIds,
    //     @Param("keyword") String keyword,
    //     Pageable pageable
    // );

    /**
     * Lấy danh sách ProductVersion đang có khuyến mãi còn hiệu lực (startAt <= now <= endAt),
     * có phân trang, và cho phép lọc thêm theo brand/category/keyword (có thể truyền null để bỏ lọc).
     * Chỉ lấy các bản ghi chưa bị xóa ở ProductVersion, Product và Promotion.
     */
    @Query(
        value = """
            SELECT pv
            FROM ProductVersion pv
            JOIN Product p ON p.id = pv.productId
            LEFT JOIN Promotion pr ON pr.id = pv.promotionId
            WHERE pv.isDeleted = false
              AND p.isDeleted = false
              AND (
                    :now IS NULL
                 OR (
                        pv.promotionId IS NOT NULL
                    AND pr.isDeleted = false
                    AND pr.startAt <= :now
                    AND pr.endAt >= :now
                 )
              )
              AND (:brandIds IS NULL OR p.brandId IN :brandIds)
              AND (:categoryIds IS NULL OR p.categoryId IN :categoryIds)
              AND (:keyword IS NULL OR pv.fullTextSearch LIKE :keyword)
        """
    )
    Page<ProductVersion> searchProductVersion(
        @Param("now") LocalDateTime now,
        @Param("brandIds") List<String> brandIds,
        @Param("categoryIds") List<String> categoryIds,
        @Param("keyword") String keyword,
        Pageable pageable
    );
}
