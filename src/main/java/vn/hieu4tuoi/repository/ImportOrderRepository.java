package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hieu4tuoi.model.ImportOrder;

public interface ImportOrderRepository extends JpaRepository<ImportOrder, String> {
}
