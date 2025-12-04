package vn.hieu4tuoi.service.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.hieu4tuoi.Security.CustomUserDetails;
import vn.hieu4tuoi.Security.SecurityUtils;
import vn.hieu4tuoi.common.OpenAIToolProvider;
import vn.hieu4tuoi.common.RoleChat;
import vn.hieu4tuoi.common.ToolCallType;
import vn.hieu4tuoi.dto.request.chatbot.ChatRequest;
import vn.hieu4tuoi.dto.request.chatbot.ChatbotRequest;
import vn.hieu4tuoi.dto.request.chatbot.UserChatRequest;
import vn.hieu4tuoi.dto.request.hybrid.HybridRagSearchRequest;
import vn.hieu4tuoi.dto.request.order.OrderByAIRequest;
import vn.hieu4tuoi.dto.respone.chat.AIResponse;
import vn.hieu4tuoi.dto.respone.hybrid.HybridRagSearchResponse;
import vn.hieu4tuoi.common.PaymentMethod;
import vn.hieu4tuoi.dto.respone.order.OrderByAIResponse;
import vn.hieu4tuoi.exception.ResourceNotFoundException;
import vn.hieu4tuoi.exception.UnauthorizedException;
import vn.hieu4tuoi.model.ChatHistory;
import vn.hieu4tuoi.model.Function;
import vn.hieu4tuoi.model.ToolCall;
import vn.hieu4tuoi.repository.ChatHistoryRepository;
import vn.hieu4tuoi.repository.UserRepository;
import vn.hieu4tuoi.service.BankService;
import vn.hieu4tuoi.service.ChatHistoryService;
import vn.hieu4tuoi.service.HybridRagService;
import vn.hieu4tuoi.service.OrderService;
import vn.hieu4tuoi.service.ProductService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CHAT_BOT_SERVICE")
public class ChatbotServiceImpl {
    @Value("${openai.api.key}")
    private String apiKey;
    @Value("${openai.api.url}")
    private String apiUrl;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final ProductService productService;
    private final UserRepository userRepository;
    private final ChatHistoryService chatHistoryService;
    private final ChatHistoryRepository chatHistoryRepository;
    private final OpenAIToolProvider toolListProvider;
    private final OrderService orderService;
    private final HybridRagService hybridRagService;
    // private final InvoiceService invoiceService;
    // lay thong tin thanh toan tu properites
    @Value("${bank.api.account-number}")
    private String accountNumber;
    private String branch = "Chi nhánh Hà Nội";
    private String bank = "MBBank";
    private String name = "Phạm Huy Tuấn";
    private final BankService bankService;

    @Transactional
    public String getChatResponse(UserChatRequest req) {
        if (req.getContent() == null || req.getContent().isEmpty()) {
            throw new RuntimeException("Nội dung không được để trống");
        }
        // lấy user từ security context
        CustomUserDetails user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để sử dụng chức năng này");
        }
        String userId = user.getId();

        String systemPrompt = """
                        Bạn là một trợ lý chatbot dành cho website bán đồ công nghệ SmartMall, (chỉ đồ mới gồm: điện thoại, tablet, tai nghe, smartwatch). Chỉ trả lời các thông tin liên quan đến sản phẩm.

                Output Verbosity: Giới hạn độ dài câu trả lời tối đa 2 đoạn ngắn hoặc 4 ý gạch đầu dòng, mỗi ý không quá 1 dòng.  Ưu tiên trả lời ngắn gọn nhưng đầy đủ, rõ ràng và dễ hiểu trong phạm vi độ dài đã nêu. Nếu khách hỏi nhiều ý, hãy đảm bảo trả lời trọn vẹn các ý trong giới hạn này.

                Một số lưu ý cho từng hàm:
                - Hàm Product_Consulting: message là tin nhắn tư vấn sẽ được gửi trực tiếp đến khách hàng, tư vấn ngắn gọn, rõ ràng, dễ hiểu dựa trên dữ liệu sản phẩm từ hàm Search_Product và từ nhu cầu của khách hàng.
                - Hàm Order: không được hỏi số lượng sản phẩm. BẮT BUỘC follow theo quy trình sau: Hỏi màu sắc, số điện thoại, địa chỉ, ghi chú ( nếu có, chỉ hỏi 1 lần ) -> Sau khi có đầy đủ thông tin thì gửi lại thông tin đơn hàng và hỏi phương thức thanh toán để xác nhận đơn hàng-> Sau đó gọi hàm order.
                        """;

        // lay ds lich su chat trong ngay cua customer
        List<ChatbotRequest.Message> chatHistories = chatHistoryService.getRecentChatHistoies(userId).stream().map(
                chatHistoryResponse -> {
                    return ChatbotRequest.Message.builder()
                            .role(chatHistoryResponse.getRole())
                            .content(chatHistoryResponse.getContent())
                            .tool_calls(chatHistoryResponse.getToolCalls())
                            .tool_call_id(chatHistoryResponse.getToolCallId())
                            .build();
                }).collect(Collectors.toList());

        List<ChatbotRequest.Message> messages = new ArrayList<>();
        messages.add(ChatbotRequest.Message.builder()
                .role("system")
                .content(systemPrompt)
                .build());
        // them lich su chat vao request
        for (ChatbotRequest.Message chatHistory : chatHistories) {
            messages.add(ChatbotRequest.Message.builder()
                    .role(chatHistory.getRole())
                    .content(chatHistory.getContent())
                    .tool_calls(chatHistory.getTool_calls() == null || chatHistory.getTool_calls().isEmpty() ? null
                            : chatHistory.getTool_calls())
                    .tool_call_id(chatHistory.getTool_call_id())
                    .build());
        }
        // luu lich su chat user
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setContent(req.getContent());
        chatRequest.setRole(RoleChat.user);
        chatRequest.setUserId(userId);
        chatRequest.setToolCallId(null);
        chatRequest.setToolCalls(null);
        chatRequest.setHidden(false);
        chatHistoryService.saveAndFlush(chatRequest);

        messages.add(ChatbotRequest.Message.builder()
                .role("user")
                .content(req.getContent())
                .build());

        // 3. Gọi API
        return CallAIAPi(messages, req, userId);
    }

    @Transactional
    private String CallAIAPi(List<ChatbotRequest.Message> messages, UserChatRequest req, String userId) {
        // 3. Thiết lập headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // chuan bi request
        ChatbotRequest request = ChatbotRequest.builder()
                .model("gpt-5.1-2025-11-13")
                .messages(messages)
                .tools(toolListProvider.getToolList())
                .tool_choice("auto")
                .max_completion_tokens(500)
                .temperature(0.7)
                .top_p(0.85)
                .build();
        HttpEntity<ChatbotRequest> entity = new HttpEntity<>(request, headers);
        // 4. Gọi API
        try {
            ResponseEntity<AIResponse> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    AIResponse.class);

            // 5. Xử lý response
            if (response.getStatusCode() == HttpStatus.OK &&
                    response.getBody() != null &&
                    !response.getBody().getChoices().isEmpty()) {
                AIResponse.Choice.Message messageResponseFromAI = response.getBody().getChoices().get(0).getMessage();
                // neu co tool call thi goi ham
                if (messageResponseFromAI.getTool_calls() != null && !messageResponseFromAI.getTool_calls().isEmpty()) {
                    // neu tool la get ds food
                    String toolName = messageResponseFromAI.getTool_calls().get(0).getFunction().getName();
                    // neu tool la order
                    // else if (toolName.equals("Order")) {
                    // //chuyen đôi arguments từ json sang mảng orderByAIRequest
                    // List<OrderItemByAIRequest> orderItemByAIRequestList = new ArrayList<>();
                    // try {
                    // String arguments =
                    // messageResponseFromAI.getTool_calls().get(0).getFunction().getArguments();
                    // // Parse the JSON wrapper first
                    // OrderByAIRequest orderWrapper = objectMapper.readValue(arguments,
                    // OrderByAIRequest.class);
                    // // Then get the items from the wrapper
                    // if (orderWrapper != null && orderWrapper.getItems() != null) {
                    // orderItemByAIRequestList = orderWrapper.getItems();
                    // }
                    // } catch (Exception e) {
                    // log.error("Lỗi khi chuyển đổi arguments: {}", e.getMessage());
                    // return "Lỗi khi chuyển đổi dữ liệu đặt hàng.";
                    // }
                    // //goi ham order
                    // Long orderId = orderService.saveOrderByAI(OrderByAIRequest.builder()
                    // .customerId(req.getCustomerId())
                    // .diningTableId(req.getDiningTableId())
                    // .items(orderItemByAIRequestList)
                    // .build());
                    // log.info("Order ID: {}", orderId);
                    // return handleFunctionCall(messages, req, messageResponseFromAI, "Đặt hàng
                    // thành công");
                    // }

                    // neu tool la Search_Product
                    if (toolName.equals("Search_Product")) {
                        // lấy tham số từ tool call
                        String arguments = messageResponseFromAI.getTool_calls().get(0).getFunction().getArguments();
                        // chuyển đổi arguments từ json sang mảng searchProductByAIRequest
                        HybridRagSearchRequest hybridRagSearchRequest = objectMapper.readValue(arguments,
                                HybridRagSearchRequest.class);
                        // gọi ham search product
                        List<HybridRagSearchResponse> searchProduct = hybridRagService
                                .searchProducts(hybridRagSearchRequest);
                        // format sang json
                        String searchProductJson = "";
                        searchProductJson = objectMapper.writeValueAsString(searchProduct);
                        return handleFunctionCall(messages, req, messageResponseFromAI,
                                "Gọi hàm Product_Consulting để tư vấn sản phẩm phù hợp nhất: " + searchProductJson,
                                userId, true);
                    }

                    // neu tool la Product_Consulting
                    else if (toolName.equals("Product_Consulting")) {
                        return handleFunctionCall(messages, req, messageResponseFromAI,
                                "done Product_Consulting", userId, false);
                        // ngắn chặn open ai sinh ra cầu trả lời thừa sau khi kết thúc hàm
                        // Product_Consulting, tự sinh ra tin nhắn thủ công

                    }
                    // neu tool la Order - xử lý đặt hàng qua AI
                    else if (toolName.equals("Order")) {
                        // Parse arguments từ tool call
                        String arguments = messageResponseFromAI.getTool_calls().get(0).getFunction().getArguments();
                        OrderByAIRequest orderByAIRequest = null;
                        try {
                            orderByAIRequest = objectMapper.readValue(arguments, OrderByAIRequest.class);
                        } catch (Exception e) {
                            log.error("Lỗi khi chuyển đổi arguments Order: {}", e.getMessage());
                            return handleFunctionCall(messages, req, messageResponseFromAI,
                                    "Lỗi khi chuyển đổi dữ liệu đặt hàng. Vui lòng thử lại.", userId, true);
                        }

                        // Gọi hàm đặt hàng
                        String orderResult = orderService.createOrderByAI(orderByAIRequest);

                        // Kiểm tra nếu đặt hàng thành công (chứa 'order success')
                        if (orderResult.contains("order success")) {
                            try {
                                // Parse JSON về đối tượng OrderByAIResponse
                                OrderByAIResponse orderResponse = objectMapper.readValue(orderResult,
                                        OrderByAIResponse.class);

                                if (orderResponse.getPaymentMethod() != null) {
                                    // Kiểm tra nếu là thanh toán chuyển khoản
                                    if (orderResponse.getPaymentMethod() == PaymentMethod.BANK_TRANSFER) {
                                        // Tạo QR code cho thanh toán
                                        String qrCodeUrl = String.format(
                                                "https://qr.sepay.vn/img?acc=%s&bank=%s&amount=%s&des=%s&template=qronly&download=DOWNLOAD",
                                                accountNumber, bank, orderResponse.getTotalPrice(),
                                                orderResponse.getOrderId());

                                        // Thêm thông tin QR code vào response
                                        orderResponse.setMessage(
                                                orderResponse.getMessage() + " QR Code thanh toán: " + qrCodeUrl);
                                        // String resultWithQR = objectMapper.writeValueAsString(orderResponse);

                                        // tạo tin nhắn tư vấn thủ công với tin nhắn đầu là vui lònng quét qr để thanh
                                        // toán và tin nhắn thứ 2 là link qr code
                                        ChatRequest chatRequest = new ChatRequest();
                                        chatRequest.setContent("Vui lòng quét qr để thanh toán");
                                        chatRequest.setRole(RoleChat.assistant);
                                        chatRequest.setUserId(userId);
                                        chatRequest.setToolCallId(null);
                                        chatRequest.setToolCalls(null);
                                        chatRequest.setHidden(false);
                                        chatHistoryService.saveAndFlush(chatRequest);
                                        
                                        ChatRequest chatRequest2 = new ChatRequest();
                                        chatRequest2.setContent(qrCodeUrl);
                                        chatRequest2.setRole(RoleChat.qr_code);
                                        chatRequest2.setUserId(userId);
                                        chatRequest2.setToolCallId(null);
                                        chatRequest2.setToolCalls(null);
                                        chatRequest2.setHidden(false);
                                        chatHistoryService.saveAndFlush(chatRequest2);

                                        // return handleFunctionCall(messages, req, messageResponseFromAI, resultWithQR,
                                        // userId, true);
                                    } else {
                                        ChatRequest chatRequest3 = new ChatRequest();
                                        chatRequest3.setContent(
                                                "Đơn hàng đã được đặt, vui lòng chuẩn bị sẵn tiền mặt để thanh toán");
                                        chatRequest3.setRole(RoleChat.assistant);
                                        chatRequest3.setUserId(userId);
                                        chatRequest3.setToolCallId(null);
                                        chatRequest3.setToolCalls(null);
                                        chatRequest3.setHidden(false);
                                        chatHistoryService.saveAndFlush(chatRequest3);
                                    }

                                    return "done Order";
                                    // neu phuong thuc thanh toan khong hop le
                                } else {
                                    ChatRequest chatRequest = new ChatRequest();
                                    chatRequest.setContent(
                                            "Phương thức thanh toán không hợp lệ.");
                                    chatRequest.setRole(RoleChat.assistant);
                                    chatRequest.setUserId(userId);
                                    chatRequest.setToolCallId(null);
                                    chatRequest.setToolCalls(null);
                                    chatRequest.setHidden(false);
                                    chatHistoryService.saveAndFlush(chatRequest);
                                    return "order failed";
                                }
                                // có lỗi khi parse json ( lỗi ko rõ nguyên nhân)
                            } catch (Exception e) {
                                ChatRequest chatRequest = new ChatRequest();
                                chatRequest.setContent(
                                        "Có lỗi xảy ra khi đặt hàng.");
                                chatRequest.setRole(RoleChat.assistant);
                                chatRequest.setUserId(userId);
                                chatRequest.setToolCallId(null);
                                chatRequest.setToolCalls(null);
                                chatRequest.setHidden(false);
                                chatHistoryService.saveAndFlush(chatRequest);
                                return "order failed";
                            }
                        }
                        // truong hop ko chứa succes -> order lỗi -> ai gen lõi dựa trên result cung cấp
                        return handleFunctionCall(messages, req, messageResponseFromAI, orderResult, userId, true);
                    }
                    // //neu ham ham lay ds order
                    // else if (toolName.equals("Get_Order_List")) {
                    // //lay ds order cua ban an
                    // List<OrderResponse> orderResponseList =
                    // orderService.getOrderByDiningTableId(req.getDiningTableId());
                    // String orderListJsonFormat =
                    // orderResponseList.stream().map(OrderResponse::toJson).collect(Collectors.joining(",
                    // ", "[", "]"));
                    // log.info("Json orderList:" + orderListJsonFormat);
                    // return handleFunctionCall(messages, req, messageResponseFromAI, "Gửi thông
                    // tin đơn hàng đến cho khách hàng. Trường hợp có món nào chưa kịp giao và đang
                    // ở trạng thái PENDING thì không tính tiền món đó nhưng báo cho khách hàng có
                    // thể đợi nấu xong để giao. Gửi lại cho khách hàng thông tin hóa đơn gồm ( món
                    // ăn x số lương x giá, tổng tiền) - Mỗi món ăn 1 hàng. Danh sách đơn hàng gồm:
                    // " + orderListJsonFormat);
                    // }
                    // //neu la ham pay(thanh toan)
                    // else if (toolName.equals("Pay")) {
                    // String arguments =
                    // messageResponseFromAI.getTool_calls().get(0).getFunction().getArguments();
                    // String paymentMethod = null;
                    // try {
                    // JsonNode root = objectMapper.readTree(arguments);
                    // if (root.has("payment_method")) {
                    // paymentMethod = root.get("payment_method").asText();
                    // }
                    // } catch (Exception e) {
                    // log.error("Lỗi khi parse JSON arguments: {}", e.getMessage(), e);
                    // return "Lỗi khi chuyển đổi dữ liệu thanh toán.";
                    // }
                    // if (paymentMethod.equals("bank")) {
                    // //lay thong tin hoa don cua ban hien tai
                    // InvoiceResponse invoiceResponse =
                    // invoiceService.getCurrentTableInvoice(req.getDiningTableId());
                    // String qrCodeUrl =
                    // String.format("https://qr.sepay.vn/img?acc=%s&bank=%s&amount=%s&des=%s&template=qronly&download=DOWNLOAD",
                    // accountNumber, bank, invoiceResponse.getTotalPrice(),
                    // invoiceResponse.getId());
                    // return handleFunctionCall(messages, req, messageResponseFromAI, "BẮT BUỘC
                    // PHẢI gi JSON object này đến khách hàng, KHÔNG gửi thêm thông tin gì khác
                    // {\"message\": \"Vui lòng quét mã QR để thanh toán\", \"imgQRCode\": \"" +
                    // qrCodeUrl + "\"}");
                    // } else if (paymentMethod.equals("cash")) {
                    // return handleFunctionCall(messages, req, messageResponseFromAI, "Nhắc khách
                    // hàng ra quầy để thanh toán");
                    // } else {
                    // return handleFunctionCall(messages, req, messageResponseFromAI, "Phương thức
                    // thanh toán không hợp lệ.");
                    // }
                    // }
                } else {
                    // neu không có tool call thì trả về tin nhắn bình thường
                    // luu doan chat assistant vao db
                    ChatRequest chatAssistantRequest = new ChatRequest();
                    chatAssistantRequest.setContent(messageResponseFromAI.getContent());
                    chatAssistantRequest.setRole(RoleChat.assistant);
                    chatAssistantRequest.setUserId(userId);
                    chatAssistantRequest.setToolCallId(null);
                    chatAssistantRequest.setToolCalls(null);
                    chatAssistantRequest.setHidden(false);
                    chatHistoryService.saveAndFlush(chatAssistantRequest);
                    String messageResponse = messageResponseFromAI.getContent();
                    return messageResponse;
                }
            } else {
                // Xử lý lỗi từ API
                log.error("Lỗi từ API: {}", response.getStatusCode());
                return "Xin lỗi, không thể xử lý yêu cầu của bạn lúc này.";
            }
        } catch (Exception e) {
            // Xử lý lỗi
            throw new RuntimeException("Lỗi khi gọi OPEN AI API: " + e.getMessage(), e);
        }
        return "Xin lỗi, không thể xử lý yêu cầu của bạn lúc này.";
    }

    // hàm xử lý function call

    private String handleFunctionCall(List<ChatbotRequest.Message> messages, UserChatRequest req,
            AIResponse.Choice.Message messageResponseFromAI, String contentToolCall, String userId, Boolean hidden) {
        // tin nhan yêu cầu gọi hàm từ AI
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setContent(null);
        chatRequest.setRole(RoleChat.assistant);
        chatRequest.setUserId(userId);
        chatRequest.setToolCallId(null);
        // convert tu request cua AI sang request dung de gọi ham save chatHistory
        List<ToolCall> toolCallRequestList = new ArrayList<>();
        for (AIResponse.Choice.ToolCall toolCallResponseFromAI : messageResponseFromAI.getTool_calls()) {
            ToolCall toolCallRequest = new ToolCall();
            toolCallRequest.setId(toolCallResponseFromAI.getId());
            toolCallRequest.setType(ToolCallType.function);
            toolCallRequest.setFunction(Function.builder()
                    .name(toolCallResponseFromAI.getFunction().getName())
                    .arguments(toolCallResponseFromAI.getFunction().getArguments())
                    .build());
            toolCallRequestList.add(toolCallRequest);
        }
        chatRequest.setToolCalls(toolCallRequestList);
        chatRequest.setHidden(hidden);
        // luu doan chat yeu cau goi ham vao db
        chatHistoryService.saveAndFlush(chatRequest);

        // add yêu cầu gọi hàm từ AI vào lịch sử chat
        ChatbotRequest.Message MessageOfFunctionCallRequestFromAI = ChatbotRequest.Message.builder()
                .role("assistant")
                .content(null)
                .tool_calls(toolCallRequestList)
                .tool_call_id(null)
                .build();
        messages.add(MessageOfFunctionCallRequestFromAI);

        ChatbotRequest.Message toolMessageResponseToAI = ChatbotRequest.Message.builder()
                .role("tool")
                .content(contentToolCall)
                .tool_call_id(messageResponseFromAI.getTool_calls().get(0).getId())
                .tool_calls(null)
                .build();
        messages.add(toolMessageResponseToAI);

        // luu doan chat tool call vao db
        ChatRequest chatToolCallRequest = new ChatRequest();
        chatToolCallRequest.setContent(contentToolCall);
        chatToolCallRequest.setRole(RoleChat.tool);
        chatToolCallRequest.setUserId(userId);
        chatToolCallRequest.setToolCallId(messageResponseFromAI.getTool_calls().get(0).getId());
        log.info("TOOL CALL ID: {}", messageResponseFromAI.getTool_calls().get(0).getId());
        chatToolCallRequest.setToolCalls(null);
        chatToolCallRequest.setHidden(true);
        chatHistoryService.saveAndFlush(chatToolCallRequest);

        // gửi dữ liệu đến AI
        if (contentToolCall.equals("done Product_Consulting")) {
            // gửi tin nhắn thủ công và lưu lại vào db
            ChatRequest chatDoneRequest = new ChatRequest();
            chatDoneRequest.setContent("done Product_Consulting");
            chatDoneRequest.setRole(RoleChat.assistant);
            chatDoneRequest.setUserId(userId);
            chatDoneRequest.setToolCallId(null);
            chatDoneRequest.setToolCalls(null);
            chatDoneRequest.setHidden(true);
            chatHistoryService.saveAndFlush(chatDoneRequest);
            return "done Product_Consulting";
        }
        return CallAIAPi(messages, req, userId);
    }

    // check trang thai thanh toan đơn hàng, tạo tin nhắn thành công nếu thành công
    @Transactional
    public Boolean checkPaymentStatus(Long chatHistoryId) {
        String userId = SecurityUtils.getCurrentUserId();

        ChatHistory chatHistory = chatHistoryRepository.findByIdAndUserId(chatHistoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat history not found with id: " + chatHistoryId));
        // trích xuất orderId từ content của chatHistory (qrCodeUrl), orderId chính là
        // tham số 'des' trong link QR
        String orderId = null;
        try {
            URI qrUri = new URI(chatHistory.getContent());
            String query = qrUri.getQuery(); // acc=...&bank=...&amount=...&des=ORDER_ID&template=...
            if (query != null) {
                String[] params = query.split("&");
                for (String param : params) {
                    if (param.startsWith("des=")) {
                        orderId = param.substring("des=".length());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Lỗi khi parse QR url để lấy orderId: {}", e.getMessage());
        }

        if (orderId == null || orderId.isEmpty()) {
            return false;
        }

        if (bankService.isValidBank(orderId)) {
            // qr cũ và thay bằng image default, thêm tin nhắn thành công
            ChatRequest chatRequest = new ChatRequest();
            chatRequest.setContent(
                    "Đơn hàng đã thanh toán thành công, chúng tôi sẽ giao hàng sớm nhất có thể, cảm ơn bạn đã tin tưởng sử dụng dịch vụ!");
            chatRequest.setRole(RoleChat.assistant);
            chatRequest.setUserId(userId);
            chatRequest.setToolCallId(null);
            chatRequest.setToolCalls(null);
            chatRequest.setHidden(false);
            chatHistoryService.saveAndFlush(chatRequest);

            chatHistory.setContent(null);
            chatHistory.setRole(RoleChat.payment_success);
            chatHistoryRepository.save(chatHistory);

            return true;
        }
        return false;
    }
}