package vn.hieu4tuoi.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import vn.hieu4tuoi.common.RoleChat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "chat_history")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatHistory {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    @Enumerated(EnumType.STRING)
    private RoleChat role;
    //quan hệ n-1 với customer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    //quan hệ 1-n với toolcall
    @OneToMany(mappedBy = "chatHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ToolCall> toolCalls;
    private String toolCallId;
    //thời gian tạo
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Boolean hidden;

    public void addToolCall(ToolCall toolCall) {
        if (toolCalls == null) {
            toolCalls = new ArrayList<>();
        }
        toolCalls.add(toolCall);
        toolCall.setChatHistory(this);
    }

}

