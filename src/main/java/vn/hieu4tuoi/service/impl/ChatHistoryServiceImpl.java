package vn.hieu4tuoi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.hieu4tuoi.Security.SecurityUtils;
import vn.hieu4tuoi.common.RoleChat;
import vn.hieu4tuoi.dto.request.chatbot.ChatRequest;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.respone.chat.ChatHistoryResponse;
import vn.hieu4tuoi.dto.respone.chat.ProductConsultingArguments;
import vn.hieu4tuoi.dto.respone.product.ProductVersionResponse;
import vn.hieu4tuoi.exception.ResourceNotFoundException;
import vn.hieu4tuoi.exception.UnauthorizedException;
import vn.hieu4tuoi.mapper.ChatHistoryMapper;
import vn.hieu4tuoi.model.ChatHistory;
import vn.hieu4tuoi.model.ToolCall;
import vn.hieu4tuoi.model.User;
import vn.hieu4tuoi.repository.ChatHistoryRepository;
import vn.hieu4tuoi.repository.UserRepository;
import vn.hieu4tuoi.service.ChatHistoryService;
import vn.hieu4tuoi.service.ProductService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatHistoryServiceImpl implements ChatHistoryService {
    private final ChatHistoryRepository chatHistoryRepository;
    private final UserRepository userRepository;
    private final ChatHistoryMapper chatHistoryMapper;
    private final ObjectMapper objectMapper;
    private final ProductService productService;

    @Override
    @Transactional
    public Long save(ChatRequest request) {
        // Find the customer or throw an exception if not found
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        ChatHistory chatHistory = new ChatHistory();
        // Map the request to the entity
        chatHistory.setContent(request.getContent());
        chatHistory.setUser(user);
        chatHistory.setRole(request.getRole());
        // lan luot set chat vao cac toolcall
        for (ToolCall toolCall : request.getToolCalls()) {
            chatHistory.addToolCall(toolCall);
        }

        chatHistoryRepository.save(chatHistory);

        return chatHistory.getId();
    }

    @Override
    public Long saveAndFlush(ChatRequest request) {
        // Find the customer or throw an exception if not found
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        ChatHistory chatHistory = new ChatHistory();
        // Map the request to the entity
        chatHistory.setContent(request.getContent());
        chatHistory.setUser(user);
        chatHistory.setRole(request.getRole());
        chatHistory.setHidden(request.getHidden());
        if (request.getToolCallId() != null) {
            chatHistory.setToolCallId(request.getToolCallId());
        }
        // lan luot set chat vao cac toolcallif
        if (request.getToolCalls() != null) {
            for (ToolCall toolCall : request.getToolCalls()) {
                chatHistory.addToolCall(toolCall);
            }
        }

        chatHistoryRepository.saveAndFlush(chatHistory);

        return chatHistory.getId();
    }

    /****** */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<List<ChatHistoryResponse>> getChatHistoriesByUserId(int page, int size) {
        // lấy user từ security context
        String userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để sử dụng chức năng này");
        }
        Pageable pageable = PageRequest.of(page, size);

        Page<ChatHistory> chatHistories = chatHistoryRepository.findByUserIdAndHiddenOrderByCreatedAtDesc(userId, false,
                pageable);

        // chuyen chat history sang dto
        // List<ChatHistoryResponse> chatHistoryResponses =
        // chatHistories.getContent().stream()
        // .map(chatHistoryMapper::toDto)
        // .toList();
        List<ChatHistoryResponse> chatHistoryResponses = new ArrayList<>();
        for (ChatHistory chatHistory : chatHistories.getContent()) {
            // nếu function Product_Consulting
            if (chatHistory.getToolCalls() == null || chatHistory.getToolCalls().isEmpty()) {
                ChatHistoryResponse chatHistoryResponse = chatHistoryMapper.toDto(chatHistory);
                chatHistoryResponses.add(chatHistoryResponse);
            } else if (chatHistory.getToolCalls().get(0).getFunction().getName().equals("Product_Consulting")) {
                try {
                    // tách ra 1 message tư vấn + 1 message gửi thông tin các sản phẩm theo
                    ProductConsultingArguments productConsultingArguments = objectMapper.readValue(
                            chatHistory.getToolCalls().get(0).getFunction().getArguments(),
                            ProductConsultingArguments.class);
                    List<ProductVersionResponse> productVersions = productService
                            .searchPublicProductVersionByIds(productConsultingArguments.getProductIds());
                    ChatHistoryResponse chatHistoryResponse1 = ChatHistoryResponse.builder()
                            .content(null)
                            .role(RoleChat.product_consulting.toString())
                            // .toolCalls(chatHistory.getToolCalls())
                            // .toolCallId(chatHistory.getToolCallId())
                            .productVersions(productVersions)
                            .build();
                    chatHistoryResponses.add(chatHistoryResponse1);
                    // tiep tuc add message tư vấn
                    String message = productConsultingArguments.getMessage();
                    ChatHistoryResponse chatHistoryResponse2 = ChatHistoryResponse.builder()
                            .content(message)
                            .role(RoleChat.assistant.toString())
                            // .toolCalls(chatHistory.getToolCalls())
                            // .toolCallId(chatHistory.getToolCallId())
                            .productVersions(null)
                            .build();
                    chatHistoryResponses.add(chatHistoryResponse2);

                    // tiep tuc add product

                } catch (JsonProcessingException e) {
                    // Nếu không parse được JSON, fallback về cách xử lý thông thường
                    ChatHistoryResponse chatHistoryResponse = chatHistoryMapper.toDto(chatHistory);
                    chatHistoryResponses.add(chatHistoryResponse);
                }
            }
        }

        // dao nguoc danh sach chat history
        Collections.reverse(chatHistoryResponses);

        return PageResponse.<List<ChatHistoryResponse>>builder()
                .items(chatHistoryResponses)
                .pageNo(chatHistories.getNumber())
                .pageSize(chatHistories.getSize())
                .build();
    }

    // data cung cấp context cho model AI
    @Override
    public List<ChatHistoryResponse> getRecentChatHistoies(String userId) {
        // Check if customer exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        // Get recent chat histories
        List<ChatHistory> chatHistories = chatHistoryRepository.findTop40ByUserIdAndCreatedAtBetweenOrderByIdDesc(
                userId, LocalDateTime.now().minusDays(1), LocalDateTime.now());

        // dao nguoc danh sach chat history
        Collections.reverse(chatHistories);

        // Map the entities to DTOs
        return chatHistories.stream()
                .map(chatHistoryMapper::toDto)
                .toList();
    }

    /**
     * Xóa toàn bộ lịch sử chat của user hiện tại.
     * Thực hiện:
     * - Lấy userId từ SecurityContext.
     * - Lấy tất cả ChatHistory theo userId.
     * - Gọi deleteAll để JPA thực hiện xóa theo entity, đảm bảo cascade/orphanRemoval
     *   sẽ xóa luôn các bản ghi liên quan trong tool_calls và function, tránh lỗi
     *   ràng buộc khóa ngoại.
     */
    @Override
    @Transactional
    public void deleteAllChatHistoriesOfCurrentUser() {
        String userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để sử dụng chức năng này");
        }

        List<ChatHistory> chatHistories = chatHistoryRepository.findByUserId(userId);
        if (chatHistories == null || chatHistories.isEmpty()) {
            return;
        }

        chatHistoryRepository.deleteAll(chatHistories);
    }

}
