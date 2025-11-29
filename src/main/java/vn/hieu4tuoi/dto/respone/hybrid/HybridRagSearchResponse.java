package vn.hieu4tuoi.dto.respone.hybrid;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO response cho kết quả tìm kiếm từ Hybrid RAG
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HybridRagSearchResponse {
    private String name;
    private String description;
    private Map<String, Object> metadata;
    private Double score;
}

