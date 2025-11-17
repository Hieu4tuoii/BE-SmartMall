package vn.hieu4tuoi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.hieu4tuoi.model.Authorities;
import vn.hieu4tuoi.model.User;

public interface UserRepository extends JpaRepository<User, String> {
    User findByEmailAndIsDeletedFalse(String email);
    User findByIdAndIsDeleted(String id, boolean isDeleted);
    List<User> findByAuthoritiesContaining(Authorities authorities);
    
    // Tìm kiếm user theo authority với phân trang
    // Page<User> findByAuthoritiesContainingAndIsDeleted(Authorities authorities, boolean isDeleted, Pageable pageable);
    
    // Tìm kiếm user theo authority và keyword với phân trang
    @Query("SELECT u FROM User u JOIN u.authorities a WHERE a = :authority AND u.isDeleted = false " +
           "AND (LOWER(u.email) LIKE :keyword OR LOWER(u.fullName) LIKE :keyword OR LOWER(u.phoneNumber) LIKE :keyword)")
    Page<User> searchCustomerByKeyword(@Param("authority") Authorities authority, @Param("keyword") String keyword, Pageable pageable);

    List<User> findAllByIdInAndIsDeleted(List<String> ids, boolean isDeleted);
}
