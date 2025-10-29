package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.hieu4tuoi.model.ProductItem;

public interface ProductItemRepository extends JpaRepository<ProductItem, String> {
    
}
