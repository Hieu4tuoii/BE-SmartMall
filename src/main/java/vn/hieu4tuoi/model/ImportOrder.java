package vn.hieu4tuoi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "import_order")
public class ImportOrder extends AbstractEntity {

    @Column(name = "supplier_id")
    private String supplierId;
}
