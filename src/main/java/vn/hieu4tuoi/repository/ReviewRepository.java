package vn.hieu4tuoi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.hieu4tuoi.model.Review;

public interface ReviewRepository extends JpaRepository<Review, String> {
    //get ds đánh giá theo productVersionId, có phân trang
    @Query("SELECT r FROM Review r WHERE r.productVersionId = :productVersionId AND r.isDeleted = :isDeleted")
    Page<Review> findAllByProductVersionIdAndIsDeleted(@Param("productVersionId") String productVersionId, @Param("isDeleted") boolean isDeleted, Pageable pageable);
    @Query("SELECT r FROM Review r WHERE r.productVersionId = :productVersionId AND r.isDeleted = :isDeleted")
    List<Review> findAllByProductVersionIdAndIsDeleted(String productVersionId, boolean isDeleted);
    
    /**
     * Đếm số lượng đánh giá theo rating và productVersionId
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.productVersionId = :productVersionId AND r.rating = :rating AND r.isDeleted = :isDeleted")
    Integer countByProductVersionIdAndRatingAndIsDeleted(@Param("productVersionId") String productVersionId, @Param("rating") Integer rating, @Param("isDeleted") boolean isDeleted);
    
    /**
     * Đếm tổng số đánh giá theo productVersionId
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.productVersionId = :productVersionId AND r.isDeleted = :isDeleted")
    Integer countByProductVersionIdAndIsDeleted(@Param("productVersionId") String productVersionId, @Param("isDeleted") boolean isDeleted);
    
    /**
     * Tính đánh giá trung bình theo productVersionId
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.productVersionId = :productVersionId AND r.isDeleted = :isDeleted")
    Double getAverageRatingByProductVersionIdAndIsDeleted(@Param("productVersionId") String productVersionId, @Param("isDeleted") boolean isDeleted);
}   