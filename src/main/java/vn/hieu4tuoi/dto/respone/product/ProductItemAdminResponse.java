package vn.hieu4tuoi.dto.respone.product;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hieu4tuoi.common.ProductItemStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductItemAdminResponse {
    private String id;
    private String imeiOrSerial;
    private ProductItemStatus status;
    // private Long importPrice;
    private LocalDateTime createdAt;
    private LocalDate warrantyActivationDate;
    private LocalDate warrantyExpirationDate;
}
