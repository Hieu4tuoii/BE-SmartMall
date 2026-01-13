package vn.hieu4tuoi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.hieu4tuoi.model.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    Product findByIdAndIsDeleted(String id, boolean isDeleted);

    @Query("SELECT p FROM Product p WHERE p.isDeleted = false " +
           "AND (LOWER(p.name) LIKE :keyword OR LOWER(p.model) LIKE :keyword OR LOWER(p.description) LIKE :keyword)")
    Page<Product> searchProductByKeyword(@Param("keyword") String keyword, Pageable pageable);

    //get ds sản phẩm ko bị xóa
    List<Product> findAllByIsDeleted(boolean isDeleted);

    //chỉ lấy ds sản phẩm chưa bị xóa (dùng cho catalog hiện tại)
    List<Product> findAllByIdInAndIsDeleted(List<String> ids, boolean isDeleted);

    //lấy ds sản phẩm theo ids, bao gồm cả bản ghi đã bị xóa (dùng cho join, lịch sử đơn hàng...)
    List<Product> findAllByIdIn(List<String> ids);
}
