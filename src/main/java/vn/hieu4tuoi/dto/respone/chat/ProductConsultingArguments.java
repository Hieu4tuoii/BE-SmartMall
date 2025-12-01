package vn.hieu4tuoi.dto.respone.chat;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import vn.hieu4tuoi.common.RoleChat;
import vn.hieu4tuoi.dto.respone.product.ProductVersionResponse;
import vn.hieu4tuoi.model.ToolCall;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductConsultingArguments {
    private String message;
    private List<String> productIds;
}
