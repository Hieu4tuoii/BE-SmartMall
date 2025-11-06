package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hieu4tuoi.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, String> {
    CartItem findByUserIdAndProductColorVersionIdAndIsDeleted(String userId, String productColorVersionId, Boolean isDeleted);
    Integer countByUserIdAndIsDeleted(String userId, Boolean isDeleted);
}
