package vn.hieu4tuoi.dto.respone.chat;

import lombok.Data;
import vn.hieu4tuoi.common.ToolCallType;

import java.util.List;

@Data
public class AIResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private Message message;

        @Data
        public static class Message {
            private String content;
            private List<ToolCall> tool_calls;
        }

        @Data
        public static class ToolCall {
            private String id;
            private ToolCallType type;
            private Function function;

            @Data
            public static class Function {
                private String name;
                private String arguments;
            }
        }
    }
}
