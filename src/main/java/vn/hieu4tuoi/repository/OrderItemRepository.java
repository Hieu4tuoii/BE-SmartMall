package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hieu4tuoi.model.OrderItem;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

    //tìm theo orderid
    List<OrderItem> findByOrderIdInAndIsDeleted(List<String> orderIds, Boolean isDeleted);
    List<OrderItem> findByOrderIdAndIsDeleted(String orderId, Boolean isDeleted);
    List<OrderItem> findAllByIdInAndIsDeleted(List<String> ids, Boolean isDeleted);
    OrderItem findByIdAndIsDeleted(String id, Boolean isDeleted);
}
