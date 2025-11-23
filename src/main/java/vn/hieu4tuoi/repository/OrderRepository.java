package vn.hieu4tuoi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.hieu4tuoi.common.OrderStatus;
import vn.hieu4tuoi.model.Order;

public interface OrderRepository extends JpaRepository<Order, String> {
    //tìm theo full text search
    @Query("SELECT o FROM Order o WHERE (o.fullTextSearch LIKE :keyword OR o.id = :id) AND (:status is null or o.status = :status) and o.isDeleted = false")
    Page<Order> findAllByFullTextSearchOrIdAndStatus(@Param("keyword") String keyword, @Param("id") String id, @Param("status") OrderStatus status, Pageable pageable);

    Order findByIdAndIsDeleted(String id, boolean isDeleted);

    List<Order> findAllByUserIdAndIsDeletedOrderByCreatedAtDesc(String userId, boolean isDeleted);
}
