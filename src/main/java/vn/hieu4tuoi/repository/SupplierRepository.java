package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hieu4tuoi.model.SupplierEntity;

public interface SupplierRepository extends JpaRepository<SupplierEntity, String> {
}
