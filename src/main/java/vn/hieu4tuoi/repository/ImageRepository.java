package vn.hieu4tuoi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hieu4tuoi.model.Image;

public interface ImageRepository extends JpaRepository<Image, String> {
    List<Image> findAllByProductIdAndIsDeleted(String productId, Boolean isDeleted);
    List<Image> findAllByIsDefaultAndProductIdInAndIsDeleted(Boolean isDefault, List<String> productIds, Boolean isDeleted);
}
