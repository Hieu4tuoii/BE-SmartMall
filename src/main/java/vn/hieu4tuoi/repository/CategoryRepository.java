package vn.hieu4tuoi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hieu4tuoi.model.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {
    Category findByIdAndIsDeleted(String id, boolean isDeleted);
    Page<Category> findAllByIsDeleted(boolean isDeleted, Pageable pageable);
    List<Category> findAllByIsDeleted(boolean isDeleted);
}
