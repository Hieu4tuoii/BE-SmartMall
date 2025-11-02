package vn.hieu4tuoi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.hieu4tuoi.model.ImportOrder;

import java.util.Optional;

public interface ImportOrderRepository extends JpaRepository<ImportOrder, String> {
    Page<ImportOrder> findAllByIsDeletedFalse(Pageable pageable);
    Optional<ImportOrder> findByIdAndIsDeletedFalse(String id);
}
