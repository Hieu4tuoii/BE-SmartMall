package vn.hieu4tuoi.dto.request.chatbot;

import lombok.*;
import vn.hieu4tuoi.common.OpenAIToolProvider;
import vn.hieu4tuoi.model.ToolCall;

import java.util.List;

@Getter
@Setter
@Builder
public class ChatbotRequest {
    private String model;
    private List<Message> messages;
    private List<OpenAIToolProvider.Tool> tools;
    private String tool_choice = "auto";
    private double temperature;
    private int max_completion_tokens;
    private Double top_p;

    @Getter
    @Setter
    @Builder
    public static class Message {
        private String role;
        private String content;
        private List<ToolCall> tool_calls;
        private String tool_call_id;
    }
}
