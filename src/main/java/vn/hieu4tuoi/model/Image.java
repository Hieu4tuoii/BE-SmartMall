package vn.hieu4tuoi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "image")
public class Image extends AbstractEntity {

    @Column(name = "url", length = 500)
    private String url;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "product_id")
    private String productId;
}
