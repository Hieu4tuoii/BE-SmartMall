package vn.hieu4tuoi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.hieu4tuoi.common.ProductItemStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "product_item")
public class ProductItem extends AbstractEntity {

    @Column(name = "imei_or_serial", length = 100, unique = true)
    private String imeiOrSerial;

    @Column(name = "import_price", precision = 15, scale = 2)
    private BigDecimal importPrice;

    @Column(name = "status", length = 50)
    private ProductItemStatus status;

    @Column(name = "warranty_activation_date")
    private LocalDate warrantyActivationDate;

    @Column(name = "warranty_expiration_date")
    private LocalDate warrantyExpirationDate;

    @Column(name = "product_color_version_id")
    private String productColorVersionId;

    @Column(name = "import_order_id")
    private String importOrderId;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = ProductItemStatus.IN_STOCK;
        }
    }
}
