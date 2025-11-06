package vn.hieu4tuoi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hieu4tuoi.model.Brand;

public interface BrandRepository extends JpaRepository<Brand, String> {
    Brand findByIdAndIsDeleted(String id, boolean isDeleted);
    Page<Brand> findAllByIsDeleted(boolean isDeleted, Pageable pageable);
    List<Brand> findAllByIsDeletedOrderByModifiedAtAsc(boolean isDeleted);
}


