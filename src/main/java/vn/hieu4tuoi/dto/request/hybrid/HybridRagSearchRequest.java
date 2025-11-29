package vn.hieu4tuoi.dto.request.hybrid;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO request cho việc tìm kiếm sản phẩm trong Hybrid RAG
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HybridRagSearchRequest {
    @NotBlank(message = "Query không được để trống")
    private String query;
    
    private Long minPrice;
    
    private Long maxPrice;
    
    private String categoryId;
    
    private String brandId;
}

