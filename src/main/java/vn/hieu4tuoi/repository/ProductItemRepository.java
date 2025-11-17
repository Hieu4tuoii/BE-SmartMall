package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.hieu4tuoi.model.ProductItem;

import java.util.List;

public interface ProductItemRepository extends JpaRepository<ProductItem, String> {
    List<ProductItem> findByImportOrderIdAndIsDeletedFalse(String importOrderId);

    List<ProductItem> findAllByIdInAndIsDeleted(List<String> ids, boolean isDeleted);
}
