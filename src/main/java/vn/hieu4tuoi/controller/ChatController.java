package vn.hieu4tuoi.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.hieu4tuoi.dto.request.chatbot.UserChatRequest;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.respone.ResponseData;
import vn.hieu4tuoi.dto.respone.chat.ChatHistoryResponse;
import vn.hieu4tuoi.service.ChatHistoryService;
import vn.hieu4tuoi.service.impl.ChatbotServiceImpl;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatbotServiceImpl chatBotService;
    private final ChatHistoryService chatHistoryService;

    @PostMapping("/")
    public ResponseData<String> handleChatMessage(@RequestBody UserChatRequest request) {
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Get chat response successfully", chatBotService.getChatResponse(request));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Đã có lỗi xảy ra. Vui lòng thử lại sau.");
        }
    }

    //lay ds chat history (phan trang lay 20 tin nhan gan nhat)
    @GetMapping("/history")
    public ResponseData<PageResponse<List<ChatHistoryResponse>>> getChatHistory(
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "20") int size) {
        PageResponse<List<ChatHistoryResponse>> chatHistories = chatHistoryService.getChatHistoriesByUserId( page, size);
        return new ResponseData<>(HttpStatus.OK.value(), "Get chat history successfully", chatHistories);
    }

    /**
     * Xóa toàn bộ lịch sử chat của user hiện tại.
     * Đảm bảo xóa luôn các bản ghi liên quan trong bảng tool_calls và function
     * để tránh lỗi ràng buộc dữ liệu.
     */
    @DeleteMapping("/history")
    public ResponseData<Void> deleteAllChatHistoryOfCurrentUser() {
        try {
            chatHistoryService.deleteAllChatHistoriesOfCurrentUser();
            return new ResponseData<>(HttpStatus.OK.value(), "Xóa toàn bộ lịch sử chat thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Đã có lỗi xảy ra. Vui lòng thử lại sau.");
        }
    }
}
