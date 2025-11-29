package vn.hieu4tuoi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import vn.hieu4tuoi.common.ToolCallType;


@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tool_calls")
public class ToolCall {
    @Id
    private String id;
    @Enumerated(EnumType.STRING)
    private ToolCallType type;
    // 1-1 với Function
    @OneToOne(mappedBy = "toolCall", cascade = CascadeType.ALL, orphanRemoval = true)
    private Function function;
    // liên kết n-1 đến bảng ChatHistory
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_history_id", nullable = false)
    @JsonIgnore
    private ChatHistory chatHistory;

    public void setFunction(Function function) {
        this.function = function;
        if (function != null) {
            function.setToolCall(this);
        }
    }
}
