package vn.hieu4tuoi.service;

import java.util.List;

import vn.hieu4tuoi.dto.request.chatbot.ChatRequest;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.respone.chat.ChatHistoryResponse;

public interface    ChatHistoryService {
    Long save(ChatRequest request);
    Long saveAndFlush(ChatRequest request);
    PageResponse<List<ChatHistoryResponse>> getChatHistoriesByUserId(int page, int size);
    List<ChatHistoryResponse> getRecentChatHistoies(String userId);
    //find by id
    /**
     * Xóa toàn bộ lịch sử chat của user hiện tại.
     * Đảm bảo xóa luôn các bản ghi liên quan trong bảng tool_calls và function
     * thông qua cấu hình cascade/orphanRemoval của entity.
     */
    void deleteAllChatHistoriesOfCurrentUser();
}
