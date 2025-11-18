package vn.hieu4tuoi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.hieu4tuoi.model.Review;

public interface ReviewRepository extends JpaRepository<Review, String> {
    //get ds đánh giá theo productVersionId, có phân trang
    @Query("SELECT r FROM Review r WHERE r.productVersionId = :productVersionId AND r.isDeleted = false")
    Page<Review> findAllByProductVersionIdAndIsDeleted(@Param("productVersionId") String productVersionId, @Param("isDeleted") boolean isDeleted, Pageable pageable);
}   