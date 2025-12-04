package vn.hieu4tuoi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.hieu4tuoi.common.RoleChat;
import vn.hieu4tuoi.model.ChatHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    Page<ChatHistory> findByUserIdAndHiddenOrderByCreatedAtDesc(String userId, Boolean hidden, Pageable pageable);

    List<ChatHistory> findTop40ByUserIdAndRoleInAndCreatedAtBetweenOrderByIdDesc(String userId, List<RoleChat> roles, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Tìm toàn bộ lịch sử chat của một user.
     * Dùng cho chức năng xóa lịch sử chat để có thể áp dụng cascade/orphanRemoval.
     */
    List<ChatHistory> findByUserId(String userId);

    Optional<ChatHistory> findByIdAndUserId(Long id, String userId);
}
