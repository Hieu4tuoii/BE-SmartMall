package vn.hieu4tuoi.dto.request.hybrid;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HybridRagProductRequest {
    private String id;
    private String name;
    private String description;
    private Map<String, Object> metadata;
}

