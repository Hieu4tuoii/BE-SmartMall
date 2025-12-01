package vn.hieu4tuoi.dto.respone.hybrid;

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
public class ProductColorResponse {
    private String id;
    private String name;
}
