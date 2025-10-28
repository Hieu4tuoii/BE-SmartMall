package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hieu4tuoi.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
}
