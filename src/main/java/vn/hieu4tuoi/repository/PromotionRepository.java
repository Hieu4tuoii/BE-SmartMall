package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hieu4tuoi.model.Promotion;

public interface PromotionRepository extends JpaRepository<Promotion, String> {
}
