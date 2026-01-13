package vn.hieu4tuoi.repository;

import java.time.LocalDateTime;
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

    //chỉ lấy ds order chưa bị xóa
    List<Order> findAllByIdInAndIsDeleted(List<String> ids, boolean isDeleted);

    //lấy ds order theo ids, bao gồm cả bản ghi đã bị xóa (dùng cho join, lịch sử...)
    List<Order> findAllByIdIn(List<String> ids);

    /**
     * Lấy danh sách đơn hàng theo khoảng thời gian tạo và trạng thái, chỉ lấy isDeleted = false.
     */
    List<Order> findAllByStatusInAndCreatedAtBetweenAndIsDeleted(List<OrderStatus> statuses,
                                                                 LocalDateTime start,
                                                                 LocalDateTime end,
                                                                 boolean isDeleted);
}
