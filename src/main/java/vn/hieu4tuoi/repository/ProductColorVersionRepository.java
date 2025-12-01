package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import vn.hieu4tuoi.model.ProductColorVersion;

public interface ProductColorVersionRepository extends JpaRepository<ProductColorVersion, String> {
    ProductColorVersion findByIdAndIsDeleted(String id, Boolean isDeleted);
    List<ProductColorVersion> findByProductVersionIdAndIsDeletedOrderByCreatedAtAsc(String productVersionId, Boolean isDeleted);
    List<ProductColorVersion> findByProductVersionIdInAndIsDeletedOrderByCreatedAtAsc(List<String> productVersionIds, Boolean isDeleted);
    List<ProductColorVersion> findAllByIdInAndIsDeleted(List<String> ids, Boolean isDeleted);
    List<ProductColorVersion> findAllByProductVersionIdInAndIsDeletedFalse(List<String> productVersionIds);
}
