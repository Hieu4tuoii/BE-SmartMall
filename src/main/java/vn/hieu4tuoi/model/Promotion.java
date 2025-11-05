package vn.hieu4tuoi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "promotion")
public class Promotion extends AbstractEntity {

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "discount")
    private Long discount; //tính theo %

    @Column(name = "maximum_discount_amount")
    private Long maximumDiscountAmount; //tính theo VNĐ
}
