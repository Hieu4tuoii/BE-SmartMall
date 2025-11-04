package vn.hieu4tuoi.dto.respone.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO cho product version trong promotion
 * Bao gồm thông tin product và version để hiển thị
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductVersionForPromotionResponse {
    private String id; // ID của version
    private String name; // Tên đầy đủ: Product Name - Version Name
    private String productName; // Tên product
    private String versionName; // Tên version
    private String productId; // ID của product
    private String imageUrl; // Ảnh của product
}

