package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hieu4tuoi.model.Banner;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, String> {

    Banner findByIdAndIsDeleted(String id, boolean isDeleted);
    
    List<Banner> findAllByIsDeleted(boolean isDeleted);
}


