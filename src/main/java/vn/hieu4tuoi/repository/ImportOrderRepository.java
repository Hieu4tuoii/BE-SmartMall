package vn.hieu4tuoi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hieu4tuoi.model.ImportOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ImportOrderRepository extends JpaRepository<ImportOrder, String> {
    Page<ImportOrder> findAllByIsDeletedFalse(Pageable pageable);
    Optional<ImportOrder> findByIdAndIsDeletedFalse(String id);

    /**
     * Lấy danh sách phiếu nhập trong khoảng thời gian tạo, chỉ lấy isDeleted = false.
     */
    List<ImportOrder> findAllByCreatedAtBetweenAndIsDeleted(LocalDateTime start,
                                                            LocalDateTime end,
                                                            boolean isDeleted);
}
