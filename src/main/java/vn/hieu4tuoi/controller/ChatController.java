package vn.hieu4tuoi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.hieu4tuoi.dto.request.chatbot.UserChatRequest;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.respone.ResponseData;
import vn.hieu4tuoi.dto.respone.chat.ChatHistoryResponse;
import vn.hieu4tuoi.service.ChatHistoryService;
import vn.hieu4tuoi.service.impl.ChatbotServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatbotServiceImpl chatBotService;
    private final ChatHistoryService chatHistoryService;

    @PostMapping("/")
    public ResponseData<String> handleChatMessage(@RequestBody UserChatRequest request) {
            return new ResponseData<>(HttpStatus.OK.value(), "Get chat response successfully", chatBotService.getChatResponse(request));
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
            chatHistoryService.deleteAllChatHistoriesOfCurrentUser();
            return new ResponseData<>(HttpStatus.OK.value(), "Xóa toàn bộ lịch sử chat thành công");
    }

    /**
     * Kiểm tra trạng thái thanh toán đơn hàng từ chat history.
     * Nếu thanh toán thành công, sẽ cập nhật chat history và tạo tin nhắn thông báo thành công.
     * 
     * @param chatHistoryId ID của chat history chứa QR code thanh toán
     * @return ResponseData chứa Boolean: true nếu thanh toán thành công, false nếu chưa thanh toán hoặc lỗi
     */
    @GetMapping("/payment-status/{chatHistoryId}")
    public ResponseData<Boolean> checkPaymentStatus(@PathVariable Long chatHistoryId) {
            return new ResponseData<>(HttpStatus.OK.value(), "Check payment status successfully", chatBotService.checkPaymentStatus(chatHistoryId));
    }
}
