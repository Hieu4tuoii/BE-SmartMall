package vn.hieu4tuoi.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "review")
public class Review extends AbstractEntity {
    private String productVersionId;
    private String userId;
    private String comment;
    private Integer rating;
}
