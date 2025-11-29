package vn.hieu4tuoi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "function")
public class Function {
    //tu dong tang
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private String arguments;
    private String name;
    // 1-1 đến ToolCall
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_call_id", nullable = false)
    @JsonIgnore
    private ToolCall toolCall;
}

