package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hieu4tuoi.model.Order;

public interface OrderRepository extends JpaRepository<Order, String> {
}
