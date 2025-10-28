package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import vn.hieu4tuoi.model.ProductVersion;

public interface ProductVersionRepository extends JpaRepository<ProductVersion, String> {
    ProductVersion findByIdAndIsDeleted(String id, Boolean isDeleted);
    List<ProductVersion> findByProductIdAndIsDeleted(String productId, Boolean isDeleted);
}
