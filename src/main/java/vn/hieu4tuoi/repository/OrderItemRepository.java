package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hieu4tuoi.model.OrderItem;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

    //tìm theo orderid chỉ lấy bản ghi chưa bị xóa
    List<OrderItem> findByOrderIdInAndIsDeleted(List<String> orderIds, Boolean isDeleted);

    //tìm theo orderid, có thể lấy cả bản ghi đã bị xóa (dùng cho join, lịch sử...)
    List<OrderItem> findByOrderIdIn(List<String> orderIds);

    List<OrderItem> findByOrderIdAndIsDeleted(String orderId, Boolean isDeleted);

    //tìm theo danh sách id chỉ lấy bản ghi chưa bị xóa
    List<OrderItem> findAllByIdInAndIsDeleted(List<String> ids, Boolean isDeleted);

    //tìm theo danh sách id, có thể lấy cả bản ghi đã bị xóa (dùng cho join, lịch sử...)
    List<OrderItem> findAllByIdIn(List<String> ids);

    OrderItem findByIdAndIsDeleted(String id, Boolean isDeleted);
}
